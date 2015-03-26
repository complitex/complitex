package org.complitex.common.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.converter.DateConverter;
import org.complitex.common.converter.GenderConverter;
import org.complitex.common.entity.*;
import org.complitex.common.entity.Log.STATUS;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.mybatis.SqlSessionFactoryBean;
import org.complitex.common.mysql.MySqlErrors;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.service.LogBean;
import org.complitex.common.service.SessionBean;
import org.complitex.common.util.Numbers;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.util.StringCultures;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.validate.IValidator;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.common.web.component.search.SearchComponentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DomainObjectStrategy extends AbstractBean implements IStrategy {
    public final static String NS = DomainObjectStrategy.class.getName();

    private static final String RESOURCE_BUNDLE = DomainObjectStrategy.class.getName();
    private final Logger log = LoggerFactory.getLogger(DomainObjectStrategy.class);

    private static final int PERMISSIONS_CHILDREN_BATCH = 500;
    private static final int ACTIVITY_CHILDREN_BATCH = 5000;

    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private SequenceBean sequenceBean;

    @EJB
    private StringCultureBean stringCultureBean;

    @EJB
    private EntityBean entityBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private PermissionBean permissionBean;

    @EJB
    private LogBean logBean;

    public Locale getSystemLocale(){
        return stringLocaleBean.getSystemLocale();
    }

    @Override
    public String displayDomainObject(Long objectId, Locale locale) {
        return displayDomainObject(getDomainObject(objectId, true), locale);
    }

    @Override
    public boolean isSimpleAttributeType(AttributeType attributeType) {
        return attributeType.getAttributeValueTypes().size() == 1
                && SimpleTypes.isSimpleType(attributeType.getAttributeValueTypes().get(0).getValueType());
    }

    @Override
    public boolean isSimpleAttribute(Attribute attribute) {
        AttributeType attributeType = getEntity().getAttributeType(attribute.getAttributeTypeId());

        return attributeType != null && isSimpleAttributeType(attributeType);
    }

    protected String getDisableSuccess() {
        return ResourceUtil.getString(RESOURCE_BUNDLE, "disable_success", stringLocaleBean.getSystemLocale());
    }

    protected String getDisableError() {
        return ResourceUtil.getString(RESOURCE_BUNDLE, "disable_error", stringLocaleBean.getSystemLocale());
    }

    protected String getEnableSuccess() {
        return ResourceUtil.getString(RESOURCE_BUNDLE, "enable_success", stringLocaleBean.getSystemLocale());
    }

    protected String getEnableError() {
        return ResourceUtil.getString(RESOURCE_BUNDLE, "enable_error", stringLocaleBean.getSystemLocale());
    }

    @Override
    public void disable(final DomainObject object) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {
                    changeActivity(object, false);
                    log.info("The process of disabling of {} tree has been successful.", getEntityName());
                    logBean.logChangeActivity(STATUS.OK, getEntityName(), object.getObjectId(), false, getDisableSuccess());
                } catch (Exception e) {
                    log.error("The process of disabling of " + getEntityName() + " tree has been failed.", e);
                    logBean.logChangeActivity(STATUS.ERROR, getEntityName(), object.getObjectId(), false, getDisableError());
                }
                log.info("The process of disabling of {} tree took {} sec.", getEntityName(), (System.currentTimeMillis() - start) / 1000);
            }
        }).start();
    }


    protected void changeActivity(DomainObject object, boolean enable) {
        object.setStatus(enable ? StatusType.ACTIVE : StatusType.INACTIVE);
        object.setEntityName(getEntityName());

        sqlSession().update(NS + ".updateDomainObject", object);

        changeChildrenActivity(object.getObjectId(), enable);
    }

    @Override
    public void enable(final DomainObject object) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {
                    changeActivity(object, true);
                    log.info("The process of enabling of {} tree has been successful.", getEntityName());
                    logBean.logChangeActivity(STATUS.OK, getEntityName(), object.getObjectId(), true, getEnableSuccess());
                } catch (Exception e) {
                    log.error("The process of enabling of " + getEntityName() + " tree has been failed.", e);
                    logBean.logChangeActivity(STATUS.ERROR, getEntityName(), object.getObjectId(), true, getEnableError());
                }
                log.info("The process of enabling of {} tree took {} sec.", getEntityName(), (System.currentTimeMillis() - start) / 1000);
            }
        }).start();
    }


    @Override
    public void changeChildrenActivity(long parentId, boolean enable) {
        String[] childrenEntities = getLogicalChildren();
        if (childrenEntities != null) {
            for (String childEntity : childrenEntities) {
                changeChildrenActivity(parentId, childEntity, enable);
            }
        }
    }


    protected void changeChildrenActivity(long parentId, String childEntity, boolean enable) {
        IStrategy childStrategy = strategyFactory.getStrategy(childEntity);

        int i = 0;
        boolean allChildrenLoaded = false;
        while (!allChildrenLoaded) {

            Set<Long> childrenIds = findChildrenActivityInfo(parentId, childEntity, i, ACTIVITY_CHILDREN_BATCH);
            if (childrenIds.size() > 0) {
                //process children
                for (long childId : childrenIds) {
                    childStrategy.changeChildrenActivity(childId, enable);
                }

                if (childrenIds.size() < ACTIVITY_CHILDREN_BATCH) {
                    allChildrenLoaded = true;
                } else {
                    i += ACTIVITY_CHILDREN_BATCH;
                }
            } else {
                allChildrenLoaded = true;
            }
        }
        updateChildrenActivity(parentId, childEntity, !enable);
    }

    protected void loadAttributes(DomainObject object) {
        loadAttributes(null, object);
    }

    protected void loadAttributes(String dataSource, DomainObject object) {
        Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("entityName", getEntityName())
                .put("objectId", object.getObjectId())
                .build();

        List<Attribute> attributes = (dataSource == null ? sqlSession() : sqlSession(dataSource)).selectList(getLoadAttributesStatement(), params);

        //protected override
        if (dataSource == null) {
            loadStringCultures(attributes);
        }else{
            loadStringCultures(dataSource, attributes);
        }

        object.setAttributes(attributes);
    }

    protected String getLoadAttributesStatement() {
        return NS + ".selectAttributes";
    }

    protected void loadStringCultures(List<Attribute> attributes) {
        loadStringCultures(null, attributes);
    }

    protected void loadStringCultures(String dataSource, List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            if (isSimpleAttribute(attribute)) {
                if (attribute.getValueId() != null) {
                    //protected override
                    if (dataSource == null) {
                        loadStringCultures(attribute);
                    }else{
                        loadStringCultures(dataSource, attribute);
                    }
                } else {
                    attribute.setStringCultures(StringCultures.newStringCultures());
                }
            }
        }
    }

    protected void loadStringCultures(Attribute attribute) {
        loadStringCultures(null, attribute);
    }

    protected void loadStringCultures(String dataSource, Attribute attribute) {
        attribute.setStringCultures(stringCultureBean.getStringCultures(dataSource, attribute.getValueId(), getEntityName()));
    }


    @Override
    public DomainObject getDomainObject(String dataSource, Long objectId, boolean runAsAdmin) {
        if (objectId == null){
            return null;
        }

        DomainObjectFilter example = new DomainObjectFilter(objectId, getEntityName());

        if (!runAsAdmin) {
            prepareExampleForPermissionCheck(example);
        } else {
            example.setAdmin(true);
        }

        DomainObject object = (dataSource == null ? sqlSession(): sqlSession(dataSource)).selectOne(NS + ".selectDomainObject", example);

        if (object != null) {
            loadAttributes(dataSource, object);
            fillAttributes(dataSource, object);
            updateStringsForNewLocales(object);

            //load subject ids
            object.setSubjectIds(loadSubjects(dataSource, object.getPermissionId()));
        }

        return object;
    }

    public DomainObject getDomainObjectTree(Long objectId){
        return sqlSession().selectOne(NS + ".selectDomainObjectTree", new DomainObjectFilter(objectId, getEntityName()));
    }

    @Override
    public DomainObject getDomainObject(Long objectId, boolean runAsAdmin) {
        return getDomainObject(null, objectId, runAsAdmin);
    }

    @Override
    public Long getObjectId(String externalId) {
        return (Long) sqlSession().selectOne(NS + ".selectDomainObjectId", ImmutableMap.of("entityName", getEntityName(),
                "externalId", externalId));
    }


    protected Set<Long> loadSubjects(String dataSource, long permissionId) {
        if (permissionId == PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID) {
            return new HashSet<>(Arrays.asList(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID));
        } else {
            return permissionBean.findSubjectIds(dataSource, permissionId);
        }
    }


    protected Set<Long> loadSubjects(long permissionId) {
        return loadSubjects(null, permissionId);
    }

    protected void updateStringsForNewLocales(DomainObject object) {
        for (Attribute attribute : object.getAttributes()) {
            List<StringCulture> strings = attribute.getStringCultures();

            if (strings != null) {
                StringCultures.updateForNewLocales(strings);
            }
        }
    }

    protected void fillAttributes(DomainObject object) {
        fillAttributes(null, object);
    }

    protected void fillAttributes(String dataSource, DomainObject object) {
        List<Attribute> toAdd = new ArrayList<>();

        for (AttributeType attributeType : getEntity(dataSource).getAttributeTypes()) {
            if (!attributeType.isObsolete()) {
                if (object.getAttributes(attributeType.getId()).isEmpty()) {
                    if (attributeType.getAttributeValueTypes().size() == 1) {
                        Attribute attribute = getNewAttributeInstance();
                        AttributeValueType attributeValueType = attributeType.getAttributeValueTypes().get(0);
                        attribute.setAttributeTypeId(attributeType.getId());
                        attribute.setValueTypeId(attributeValueType.getId());
                        attribute.setObjectId(object.getObjectId());
                        attribute.setAttributeId(1L);

                        if (isSimpleAttributeType(attributeType)) {
                            attribute.setStringCultures(StringCultures.newStringCultures());
                        }
                        toAdd.add(attribute);
                    } else {
                        Attribute manyValueTypesAttribute = fillManyValueTypesAttribute(attributeType, object.getObjectId());
                        if (manyValueTypesAttribute != null) {
                            toAdd.add(manyValueTypesAttribute);
                        }
                    }
                }
            }
        }
        if (!toAdd.isEmpty()) {
            object.getAttributes().addAll(toAdd);
        }
    }

    protected Attribute getNewAttributeInstance() {
        return new Attribute();
    }

    protected Attribute fillManyValueTypesAttribute(AttributeType attributeType, Long objectId) {
        return null;
    }

    /**
     * Helper method. Prepares example for permission check.
     * @param example 
     */
    public void prepareExampleForPermissionCheck(DomainObjectFilter example) {
        boolean isAdmin = sessionBean.isAdmin();
        example.setAdmin(isAdmin);
        if (!isAdmin) {
            example.setUserPermissionString(sessionBean.getPermissionString(getEntityName()));
        }
    }

    @Override
    public List<? extends DomainObject> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);
        extendOrderBy(example);

        List<DomainObject> objects = sqlSession().selectList(NS + ".selectDomainObjects", example);

        for (DomainObject object : objects) {
            loadAttributes(object);
            //load subject ids
            object.setSubjectIds(loadSubjects(object.getPermissionId()));
        }
        return objects;
    }

    public List<? extends DomainObject> find(DomainObjectFilter example, long first, long count){
        example.setFirst(first);
        example.setCount(count);

        return getList(example);
    }

    @Override
    public Long getCount(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return 0L;
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);

        return (Long) sqlSession().selectOne(NS + ".selectDomainObjectCount", example);
    }

    /**
     * Simple wrapper around EntityBean.getEntity for convenience.
     * @return Entity description
     */
    @Override
    public Entity getEntity(String dataSource) {
        return entityBean.getEntity(dataSource, getEntityName());
    }

    @Override
    public Entity getEntity() {
        return entityBean.getEntity(getEntityName());
    }

    @Override
    public DomainObject newInstance() {
        DomainObject object = new DomainObject();

        fillAttributes(object);

        //set up subject ids to visible-by-all subject
        object.setSubjectIds(new HashSet<>(Arrays.asList(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)));

        return object;
    }


    protected void insertAttribute(Attribute attribute) {
        final List<StringCulture> strings = attribute.getStringCultures();

        if (strings != null) {
            Long generatedStringId = insertStrings(attribute.getAttributeTypeId(), strings);
            attribute.setValueId(generatedStringId);
        } else {
            //reference attribute
        }

        if (attribute.getValueId() != null || getEntity().getAttributeType(attribute.getAttributeTypeId()).isMandatory()) {
            attribute.setEntityName(getEntityName());

            sqlSession().insert(getInsertAttributeStatement(), attribute);
        }
    }

    protected String getInsertAttributeStatement() {
        return NS + ".insertAttribute";
    }


    protected Long insertStrings(long attributeTypeId, List<StringCulture> strings) {
        return stringCultureBean.save(strings, getEntityName());
    }


    @Override
    public void insert(DomainObject object, Date insertDate) {
        object.setObjectId(sequenceBean.nextId(getEntityName()));
        object.setPermissionId(getNewPermissionId(object.getSubjectIds()));
        insertDomainObject(object, insertDate);

        for (Attribute attribute : object.getAttributes()) {
            attribute.setObjectId(object.getObjectId());
            attribute.setStartDate(insertDate);
            insertAttribute(attribute);
        }
    }


    protected Long getNewPermissionId(Set<Long> newSubjectIds) {
        if (newSubjectIds.size() == 1 && newSubjectIds.contains(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)) {
            return PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID;
        } else {
            List<Subject> subjects = new ArrayList<>();
            for (Long subjectId : newSubjectIds) {
                subjects.add(new Subject("organization", subjectId));
            }
            return permissionBean.getPermission(getEntityName(), subjects);
        }
    }


    protected void insertDomainObject(DomainObject object, Date insertDate) {
        object.setStartDate(insertDate);
        object.setEntityName(getEntityName());

        sqlSession().insert(NS + ".insertDomainObject", object);
    }


    @Override
    public void archiveAttributes(Collection<Long> attributeTypeIds, Date endDate) {
        if (attributeTypeIds != null && !attributeTypeIds.isEmpty()) {
            Map<String, Object> params = ImmutableMap.<String, Object>builder().
                    put("entityName", getEntityName()).
                    put("endDate", endDate).
                    put("attributeTypeIds", attributeTypeIds).
                    build();
            sqlSession().update(NS + ".archiveAttributes", params);
        }
    }


    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        //permission comparison
        if (isNeedToChangePermission(oldObject.getSubjectIds(), newObject.getSubjectIds())) {
            newObject.setPermissionId(getNewPermissionId(newObject.getSubjectIds()));
        }

        if (!Numbers.isEqual(oldObject.getPermissionId(), newObject.getPermissionId())) {
            updatePermissionId(newObject.getObjectId(), newObject.getPermissionId());
        }

        //attributes comparison
        for (Attribute oldAttr : oldObject.getAttributes()) {
            boolean removed = true;
            for (Attribute newAttr : newObject.getAttributes()) {
                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
                    //the same attribute_type and the same attribute_id
                    removed = false;
                    boolean needToUpdateAttribute = false;

                    AttributeType attributeType = getEntity().getAttributeType(oldAttr.getAttributeTypeId());

                    Long oldValueTypeId = oldAttr.getValueTypeId();
                    Long newValueTypeId = newAttr.getValueTypeId();

                    if (!Numbers.isEqual(oldValueTypeId, newValueTypeId)) {
                        needToUpdateAttribute = true;
                    } else {
                        String attributeValueType = attributeType.getAttributeValueType(oldAttr.getValueTypeId()).getValueType();
                        if (SimpleTypes.isSimpleType(attributeValueType)) {
                            SimpleTypes simpleType = SimpleTypes.valueOf(attributeValueType.toUpperCase());
                            switch (simpleType) {
                                case STRING_CULTURE: {
                                    boolean valueChanged = false;
                                    for (StringCulture oldString : oldAttr.getStringCultures()) {
                                        for (StringCulture newString : newAttr.getStringCultures()) {
                                            //compare strings
                                            if (oldString.getLocaleId().equals(newString.getLocaleId())) {
                                                if (!Strings.isEqual(oldString.getValue(), newString.getValue())) {
                                                    valueChanged = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (valueChanged) {
                                        needToUpdateAttribute = true;
                                    }
                                }
                                break;

                                case GENDER:
                                case BOOLEAN:
                                case DATE:
                                case DATE2:
                                case MASKED_DATE:
                                case DOUBLE:
                                case INTEGER: {
                                    String oldString = oldAttr.getStringValue();
                                    String newString = newAttr.getStringValue();

                                    if (!StringUtil.isEqualIgnoreCase(oldString, newString)) {
                                        needToUpdateAttribute = true;
                                    }
                                }
                                break;

                                case BIG_STRING:
                                case STRING: {
                                    String oldString = oldAttr.getStringValue();
                                    String newString = newAttr.getStringValue();

                                    if (!Strings.isEqual(oldString, newString)) {
                                        needToUpdateAttribute = true;
                                    }
                                }
                                break;
                            }
                        } else {
                            //reference object ids
                            Long oldValueId = oldAttr.getValueId();
                            Long newValueId = newAttr.getValueId();
                            if (!Numbers.isEqual(oldValueId, newValueId)) {
                                needToUpdateAttribute = true;
                            }
                        }
                    }

                    if (needToUpdateAttribute) {
                        archiveAttribute(oldAttr, updateDate);
                        newAttr.setStartDate(updateDate);
                        newAttr.setObjectId(newObject.getObjectId());
                        insertAttribute(newAttr);
                    }
                }
            }
            if (removed) {
                archiveAttribute(oldAttr, updateDate);
            }
        }

        for (Attribute newAttr : newObject.getAttributes()) {
            boolean added = true;
            for (Attribute oldAttr : oldObject.getAttributes()) {
                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
                    //the same attribute_type and the same attribute_id
                    added = false;
                    break;
                }
            }

            if (added) {
                newAttr.setStartDate(updateDate);
                newAttr.setObjectId(newObject.getObjectId());
                insertAttribute(newAttr);
            }
        }

        if (!Objects.equals(oldObject.getParentId(), newObject.getParentId())
                || !Objects.equals(oldObject.getParentEntityId(), newObject.getParentEntityId())
                || (!Objects.equals(oldObject.getExternalId(), newObject.getExternalId()))) {
            oldObject.setStatus(StatusType.ARCHIVE);
            oldObject.setEndDate(updateDate);
            oldObject.setEntityName(getEntityName());

            sqlSession().update(NS + ".updateDomainObject", oldObject);
            insertUpdatedDomainObject(newObject, updateDate);
        }
    }


    protected void archiveAttribute(Attribute attribute, Date archiveDate) {
        attribute.setEndDate(archiveDate);
        attribute.setStatus(StatusType.ARCHIVE);
        attribute.setEntityName(getEntityName());

        sqlSession().update(NS + ".updateAttribute", attribute);
    }

    @Override
    public boolean isNeedToChangePermission(Set<Long> oldSubjectIds, Set<Long> newSubjectIds) {
        return !newSubjectIds.equals(oldSubjectIds);
    }


    @Override
    public void updateAndPropagate(DomainObject oldObject, final DomainObject newObject, Date updateDate) {
        if (!canPropagatePermissions(newObject)) {
            throw new RuntimeException("Illegal call of updateAndPropagate() as `" + getEntityName() + "` entity is not able to has children.");
        }
        update(oldObject, newObject, updateDate);

        new Thread(new Runnable() {

            @Override
            public void run() { //todo thread to async
                long start = System.currentTimeMillis();
                try {
                    propagatePermissions(newObject);
                    log.info("The process of permissions replacement for {} tree has been successful.", getEntityName());
                    logBean.logReplacePermissions(STATUS.OK, getEntityName(), newObject.getObjectId(), getReplacePermissionsSuccess());
                } catch (Exception e) {
                    log.error("The process of permissions replacement for " + getEntityName() + " tree has been failed.", e);
                    logBean.logReplacePermissions(STATUS.ERROR, getEntityName(), newObject.getObjectId(), getReplacePermissionsError());
                }
                log.info("The process of permissions replacement for {} tree took {} sec.", getEntityName(),
                        (System.currentTimeMillis() - start) / 1000);
            }
        }).start();
    }

    protected void propagatePermissions(DomainObject object) {
        replaceChildrenPermissions(object.getObjectId(), object.getSubjectIds());
    }

    protected String getReplacePermissionsError() {
        return ResourceUtil.getString(DomainObjectStrategy.class.getName(), "replace_permissions_error", stringLocaleBean.getSystemLocale());
    }

    protected String getReplacePermissionsSuccess() {
        return ResourceUtil.getString(DomainObjectStrategy.class.getName(), "replace_permissions_success", stringLocaleBean.getSystemLocale());
    }


    protected List<PermissionInfo> findChildrenPermissionInfo(long parentId, String childEntity, int start, int size) {
        Map<String, Object> params = new HashMap<>();

        params.put("entity", childEntity);
        params.put("parentId", parentId);
        params.put("parentEntity", getEntityName());
        params.put("start", start);
        params.put("size", size);

        return sqlSession().selectList(NS + ".selectChildrenPermissionInfo", params);
    }


    @Override
    public void replacePermissions(PermissionInfo objectPermissionInfo, Set<Long> subjectIds) {
        replaceObjectPermissions(objectPermissionInfo, subjectIds);
        replaceChildrenPermissions(objectPermissionInfo.getId(), subjectIds);
    }


    protected void replaceChildrenPermissions(long parentId, Set<Long> subjectIds) {
        String[] childrenEntities = getLogicalChildren();
        if (childrenEntities != null && childrenEntities.length > 0) {
            for (String childEntity : childrenEntities) {
                replaceChildrenPermissions(childEntity, parentId, subjectIds);
            }
        }
    }


    protected void replaceObjectPermissions(PermissionInfo objectPermissionInfo, Set<Long> subjectIds) {
        Set<Long> oldSubjectIds = loadSubjects(objectPermissionInfo.getPermissionId());
        if (isNeedToChangePermission(oldSubjectIds, subjectIds)) {
            long oldPermission = objectPermissionInfo.getPermissionId();
            objectPermissionInfo.setPermissionId(getNewPermissionId(subjectIds));
            long newPermission = objectPermissionInfo.getPermissionId();
            if (!Numbers.isEqual(oldPermission, newPermission)) {
                updatePermissionId(objectPermissionInfo.getId(), newPermission);
            }
        }
    }


    protected void replaceChildrenPermissions(String childEntity, long parentId, Set<Long> subjectIds) {
        IStrategy childStrategy = strategyFactory.getStrategy(childEntity);

        int i = 0;
        boolean allChildrenLoaded = false;
        while (!allChildrenLoaded) {

            List<PermissionInfo> childrenPermissionInfo = findChildrenPermissionInfo(parentId, childEntity, i, PERMISSIONS_CHILDREN_BATCH);
            if (childrenPermissionInfo.size() > 0) {
                //process children
                for (PermissionInfo childPermissionInfo : childrenPermissionInfo) {
                    childStrategy.replacePermissions(childPermissionInfo, subjectIds);
                }

                if (childrenPermissionInfo.size() < PERMISSIONS_CHILDREN_BATCH) {
                    allChildrenLoaded = true;
                } else {
                    i += PERMISSIONS_CHILDREN_BATCH;
                }
            } else {
                allChildrenLoaded = true;
            }
        }
    }


    protected void updatePermissionId(long objectId, long permissionId) {
        Map<String, Object> params = new HashMap<>();

        params.put("entity", getEntityName());
        params.put("objectId", objectId);
        params.put("permissionId", permissionId);

        sqlSession().update(NS + ".updatePermissionId", params);
    }


    protected void insertUpdatedDomainObject(DomainObject object, Date updateDate) {
        insertDomainObject(object, updateDate);
    }

    @Override
    public void archive(DomainObject object, Date endDate) {
        object.setStatus(StatusType.ARCHIVE);
        object.setEndDate(endDate);
        object.setEntityName(getEntityName());

        sqlSession().update(NS + ".updateDomainObject", object);

        Map<String, Object> params = ImmutableMap.<String, Object>builder().
                put("entityName", getEntityName()).
                put("endDate", endDate).
                put("objectId", object.getObjectId()).build();

        sqlSession().update(NS + ".archiveObjectAttributes", params);
    }

    /*
     * Search component functionality
     */
    @Override
    public int getSearchTextFieldSize() {
        return 30;
    }

    @Override
    public boolean allowProceedNextSearchFilter() {
        return false;
    }

    /*
     * List page related functionality.
     */
    /**
     * Determines column in list page by that list page's data will be sorted by default when there is no sorting column in preferences.
     */
    @Override
    public Long getDefaultSortAttributeTypeId() {
        return Collections.min(getColumnAttributeTypeIds());
    }

    /**
     *
     * @return Сортированный список идентификаторов атрибутов, которые должны выводиться в качестве колонок на странице записей.
     */
    public List<Long> getColumnAttributeTypeIds() {
        return getEntity().getAttributeTypes().stream().map(AttributeType::getId).collect(Collectors.toList());
    }

    @Override
    public List<String> getSearchFilters() {
        return null;
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return null;
    }

    @Override
    public void configureExample(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return null;
    }

    @Override
    public List<String> getParentSearchFilters() {
        return getSearchFilters();
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return null;
    }

    @SuppressWarnings({"unchecked"})

    @Override
    public EntityObjectInfo findParentInSearchComponent(long id, Date date) {
        DomainObjectFilter example = new DomainObjectFilter(id, getEntityName());

        example.setStartDate(date);
        Map<String, Object> result = sqlSession().selectOne(NS + ".selectParentInSearchComponent", example);
        if (result != null) {
            Long parentId = (Long) result.get("parentId");
            String parentEntity = (String) result.get("parentEntity");
            if (parentId != null && !Strings.isEmpty(parentEntity)) {
                return new EntityObjectInfo(parentEntity, parentId);
            }
        }
        return null;
    }

    /*
     * Helper util method.
     */
    @Override
    public SearchComponentState getSearchComponentStateForParent(Long parentId, String parentEntity, Date date) {
        if (parentId != null && parentEntity != null) {
            SearchComponentState componentState = new SearchComponentState();
            Map<String, Long> ids = new HashMap<>();

            EntityObjectInfo parentData = new EntityObjectInfo(parentEntity, parentId);
            while (parentData != null) {
                String currentParentEntity = parentData.getEntityName();
                Long currentParentId = parentData.getId();
                ids.put(currentParentEntity, currentParentId);
                parentData = strategyFactory.getStrategy(currentParentEntity).findParentInSearchComponent(currentParentId, date);
            }
            List<String> parentSearchFilters = getParentSearchFilters();
            if (parentSearchFilters != null && !parentSearchFilters.isEmpty()) {
                for (String searchFilter : parentSearchFilters) {
                    DomainObject object = new DomainObject(SearchComponentState.NOT_SPECIFIED_ID);
                    Long id = ids.get(searchFilter);
                    IStrategy searchFilterStrategy = strategyFactory.getStrategy(searchFilter);
                    if (id != null) {
                        if (date == null) {
                            DomainObject objectFromDb = searchFilterStrategy.getDomainObject(id, true);
                            if (objectFromDb != null) {
                                object = objectFromDb;
                            }
                        } else {
                            DomainObject historyObject = searchFilterStrategy.getHistoryObject(ids.get(searchFilter), date);
                            if (historyObject != null) {
                                object = historyObject;
                            }
                        }
                    }
                    componentState.put(searchFilter, object);
                }
                return componentState;
            }
        }
        return null;
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelBeforeClass() {
        return null;
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return null;
    }

    @Override
    public IValidator getValidator() {
        return null;
    }


    @Override
    public List<History> getHistory(long objectId) {
        List<History> historyList = new ArrayList<>();

        TreeSet<Date> historyDates = getHistoryDates(objectId);

        for (final Date date : historyDates) {
            DomainObject historyObject = getHistoryObject(objectId, date);
            History history = new History(date, historyObject);
            historyList.add(history);
        }

        return historyList;
    }


    @Override
    public TreeSet<Date> getHistoryDates(long objectId) {
        DomainObjectFilter example = new DomainObjectFilter(objectId, getEntityName());

        List<Date> results = sqlSession().selectList(NS + ".historyDates", example);

        return new TreeSet<>(Collections2.filter(results, new Predicate<Date>() {

            @Override
            public boolean apply(Date input) {
                return input != null;
            }
        }));
    }


    @Override
    public DomainObject getHistoryObject(long objectId, Date date) {
        DomainObjectFilter example = new DomainObjectFilter(objectId, getEntityName());
        example.setStartDate(date);

        DomainObject object = sqlSession().selectOne(NS + ".selectHistoryObject", example);
        if (object == null) {
            return null;
        }

        List<Attribute> historyAttributes = loadHistoryAttributes(objectId, date);
        loadStringCultures(historyAttributes);
        object.setAttributes(historyAttributes);
        updateStringsForNewLocales(object);
        return object;
    }


    protected List<Attribute> loadHistoryAttributes(long objectId, Date date) {
        DomainObjectFilter example = new DomainObjectFilter(objectId, getEntityName());
        example.setStartDate(date);

        return sqlSession().selectList(NS + ".selectHistoryAttributes", example);
    }

    /*
     * Description metadata
     */
    @Override
    public String[] getRealChildren() {
        return null;
    }

    @Override
    public String[] getLogicalChildren() {
        return getRealChildren();
    }

    @Override
    public String[] getParents() {
        return null;
    }

    @Override
    public String getAttributeLabel(Attribute attribute, Locale locale) {
        return entityBean.getAttributeLabel(getEntityName(), attribute.getAttributeTypeId(), locale);
    }

    @Override
    public long getDefaultOrderByAttributeId() {
        return getEntity().getId();
    }

    protected void extendOrderBy(DomainObjectFilter example) {
    }

    /*
     * Validation methods
     */
    /**
     * Default validation
     */

    @Override
    public Long performDefaultValidation(DomainObject object, Locale locale) {
        Map<String, Object> params = createValidationParams(object, locale);
        List<Long> results = sqlSession().selectList(NS + ".defaultValidation", params);

        for (Long result : results) {
            if (!result.equals(object.getObjectId())) {
                return result;
            }
        }

        return null;
    }

    protected Map<String, Object> createValidationParams(DomainObject object, Locale locale) {
        //get attribute id for unique check.
        //it supposed that unique attribute type id is first in definition of attribute types of entity so that its id is entity's id.
        long attributeTypeId = getEntity().getId();

        Attribute attribute = object.getAttribute(attributeTypeId);

        //first attribute id is entityId + 1
        if (attribute == null){
            attribute = object.getAttribute(attributeTypeId + 1);
        }

        if (attribute == null) {
            throw new IllegalStateException("Domain object(entity = " + getEntityName() + ", id = " + object.getObjectId()
                    + ") has no attribute with attribute type id = " + attributeTypeId + "!");
        }
        if (attribute.getStringCultures() == null) {
            throw new IllegalStateException("Attribute of domain object(entity = " + getEntityName() + ", id = " + object.getObjectId()
                    + ") with attribute type id = " + attributeTypeId + " and attribute id = " + attribute.getAttributeId()
                    + " has null lozalized values.");
        }
        String text = StringCultures.getValue(attribute.getStringCultures(), locale);

        Map<String, Object> params = new HashMap<>();

        params.put("entity", getEntityName());
        params.put("localeId", stringLocaleBean.convert(locale).getId());
        params.put("attributeTypeId", attributeTypeId);
        params.put("text", text);
        params.put("parentId", object.getParentId());
        params.put("parentEntityId", object.getParentEntityId());

        return params;
    }


    @Override
    public void changePermissions(PermissionInfo objectPermissionInfo, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        changeObjectPermissions(objectPermissionInfo, addSubjectIds, removeSubjectIds);
        changeChildrenPermissions(objectPermissionInfo.getId(), addSubjectIds, removeSubjectIds);
    }


    protected void changeObjectPermissions(PermissionInfo objectPermissionInfo, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        Set<Long> currentSubjectIds = loadSubjects(objectPermissionInfo.getPermissionId());
        Set<Long> newSubjectIds = new HashSet<>(currentSubjectIds);

        if (addSubjectIds != null) {
            if (addSubjectIds.contains(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)) {
                newSubjectIds.clear();
                newSubjectIds.add(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
            } else {
                if (currentSubjectIds.contains(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)) {
                    newSubjectIds.remove(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
                }
                newSubjectIds.addAll(addSubjectIds);
            }
        }
        if (removeSubjectIds != null) {
            newSubjectIds.removeAll(removeSubjectIds);
        }

        if (newSubjectIds.isEmpty()) {
            newSubjectIds.add(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
        }

        if (isNeedToChangePermission(currentSubjectIds, newSubjectIds)) {
            Long oldPermissionId = objectPermissionInfo.getPermissionId();
            Long newPermissionId = getNewPermissionId(newSubjectIds);
            if (!Numbers.isEqual(oldPermissionId, newPermissionId)) {
                updatePermissionId(objectPermissionInfo.getId(), newPermissionId);
            }
        }
    }

    @Override
    public void changePermissionsInDistinctThread(final long objectId, final long permissionId, final Set<Long> addSubjectIds,
            final Set<Long> removeSubjectIds) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {
                    PermissionInfo permissionInfo = new PermissionInfo();
                    permissionInfo.setId(objectId);
                    permissionInfo.setPermissionId(permissionId);
                    changePermissions(permissionInfo, addSubjectIds, removeSubjectIds);
                    log.info("The process of permissions change for {} tree has been successful.", getEntityName());
                    logBean.logChangePermissions(STATUS.OK, getEntityName(), objectId, getChangePermissionsSuccess());
                } catch (Exception e) {
                    log.error("The process of permissions change for " + getEntityName() + " tree has been failed.", e);
                    logBean.logChangePermissions(STATUS.ERROR, getEntityName(), objectId, getChangePermissionsError());
                }
                log.info("The process of permissions change for {} tree took {} sec.", getEntityName(), (System.currentTimeMillis() - start) / 1000);
            }
        }).start();
    }

    protected String getChangePermissionsError() {
        return ResourceUtil.getString(DomainObjectStrategy.class.getName(), "change_permissions_error", stringLocaleBean.getSystemLocale());
    }

    protected String getChangePermissionsSuccess() {
        return ResourceUtil.getString(DomainObjectStrategy.class.getName(), "change_permissions_success", stringLocaleBean.getSystemLocale());
    }


    protected void changeChildrenPermissions(long parentId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        String[] childrenEntities = getLogicalChildren();
        if (childrenEntities != null && childrenEntities.length > 0) {
            for (String childEntity : childrenEntities) {
                changeChildrenPermissions(childEntity, parentId, addSubjectIds, removeSubjectIds);
            }
        }
    }


    protected void changeChildrenPermissions(String childEntity, long parentId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        IStrategy childStrategy = strategyFactory.getStrategy(childEntity);

        int i = 0;
        boolean allChildrenLoaded = false;
        while (!allChildrenLoaded) {

            List<PermissionInfo> childrenPermissionInfo = findChildrenPermissionInfo(parentId, childEntity, i, PERMISSIONS_CHILDREN_BATCH);
            if (childrenPermissionInfo.size() > 0) {
                //process children
                for (PermissionInfo childPermissionInfo : childrenPermissionInfo) {
                    childStrategy.changePermissions(childPermissionInfo, addSubjectIds, removeSubjectIds);
                }

                if (childrenPermissionInfo.size() < PERMISSIONS_CHILDREN_BATCH) {
                    allChildrenLoaded = true;
                } else {
                    i += PERMISSIONS_CHILDREN_BATCH;
                }
            } else {
                allChildrenLoaded = true;
            }
        }
    }


    protected Set<Long> findChildrenActivityInfo(long parentId, String childEntity, int start, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("entity", childEntity);
        params.put("parentId", parentId);
        params.put("parentEntity", getEntityName());
        params.put("start", start);
        params.put("size", size);

        List<Long> results = sqlSession().selectList(NS + ".selectChildrenActivityInfo", params);

        return new HashSet<>(results);
    }


    protected void updateChildrenActivity(long parentId, String childEntity, boolean enabled) {
        Map<String, Object> params = new HashMap<>();

        params.put("entity", childEntity);
        params.put("parentId", parentId);
        params.put("parentEntity", getEntityName());
        params.put("enabled", enabled);
        params.put("status", enabled ? StatusType.INACTIVE : StatusType.ACTIVE);

        sqlSession().update(NS + ".updateChildrenActivity", params);
    }

    @Override
    public boolean canPropagatePermissions(DomainObject object) {
        return getLogicalChildren() != null && getLogicalChildren().length > 0;
    }

    @Override
    public String displayAttribute(Attribute attribute, Locale locale) {
        if (attribute == null){
            return "";
        }

        AttributeType attributeType = entityBean.getAttributeType(attribute.getAttributeTypeId());

        switch (attributeType.getAttributeValueTypes().get(0).getValueType().toUpperCase()) {
            case "STRING_CULTURE":
                return attribute.getStringValue(locale);
            case "STRING":
            case "DOUBLE":
            case "INTEGER":
                return attribute.getStringValue();
            case "BOOLEAN":
                return Boolean.valueOf(attribute.getStringValue()) ? "Да" : "Нет"; //todo resource
            case "DATE":
            case "DATE2":
            case "MASKED_DATE":
                DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", locale);
                return dateFormatter.format(new DateConverter().toObject(attribute.getStringValue()));
            case "GENDER":
                return new GenderConverter().toObject(attribute.getStringValue()).equals(Gender.MALE) ? "Муж." : "Жен."; //todo resource
        }

        return StringCultures.getValue(attribute.getStringCultures(), locale);
    }


    @Override
    public void delete(long objectId, Locale locale) throws DeleteException {
        deleteChecks(objectId, locale);
        deleteStrings(objectId);
        deleteAttribute(objectId);
        deleteDomainObject(objectId, locale);
    }


    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        childrenExistCheck(objectId, locale);
        referenceExistCheck(objectId, locale);
    }


    protected void deleteStrings(long objectId) {
        Set<Long> localizedValueTypeIds = getLocalizedValueTypeIds();
        if (localizedValueTypeIds != null && !localizedValueTypeIds.isEmpty()) {
            stringCultureBean.delete(getEntityName(), objectId, localizedValueTypeIds);
        }
    }


    protected void deleteAttribute(long objectId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entityName", getEntityName()); //todo filter
        params.put("objectId", objectId);

        sqlSession().delete(NS + ".deleteAttribute", params);
    }


    protected void deleteDomainObject(long objectId, Locale locale) throws DeleteException {
        Map<String, Object> params = new HashMap<>(); //todo filter
        params.put("entityName", getEntityName());
        params.put("objectId", objectId);

        try {
            sqlSession().delete(NS + ".deleteDomainObject", params);
        } catch (Exception e) {
            SQLException sqlException = null;
            Throwable t = e;
            while (true) {
                if (t == null) {
                    break;
                }
                if (t instanceof SQLException) {
                    sqlException = (SQLException) t;
                    break;
                }
                t = t.getCause();
            }

            if (sqlException != null && MySqlErrors.isIntegrityConstraintViolationError(sqlException)) {
                throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_error", locale));
            } else {
                throw new RuntimeException(e);
            }
        }
    }


    protected void childrenExistCheck(long objectId, Locale locale) throws DeleteException {
        String[] realChildren = getRealChildren();
        if (realChildren != null && realChildren.length > 0) {
            for (String childEntity : realChildren) {
                if (childrenExistCheck(childEntity, objectId)) {
                    throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_error", locale));
                }
            }
        }
    }


    protected boolean childrenExistCheck(String childEntity, long objectId) {
        Map<String, Object> params = new HashMap<>();
        params.put("childEntity", childEntity);
        params.put("objectId", objectId);
        params.put("entityId", getEntity().getId());

        return sqlSession().selectOne(NS + ".childrenExistCheck", params) != null;
    }


    protected Set<Long> getLocalizedValueTypeIds() {
        Set<Long> localizedValueTypeIds = new HashSet<>();

        for (AttributeType attributeType : getEntity().getAttributeTypes()) {
            for (AttributeValueType valueType : attributeType.getAttributeValueTypes()) {
                if (SimpleTypes.isSimpleType(valueType.getValueType())) {
                    localizedValueTypeIds.add(valueType.getId());
                }
            }
        }
        return localizedValueTypeIds;
    }


    protected void referenceExistCheck(long objectId, Locale locale) throws DeleteException {
        for (String entityName : entityBean.getEntityNames()) {
            Entity entity = entityBean.getEntity(entityName);
            for (AttributeType attributeType : entity.getAttributeTypes()) {
                for (AttributeValueType attributeValueType : attributeType.getAttributeValueTypes()) {
                    if (getEntityName().equals(attributeValueType.getValueType())) {
                        String referenceEntity = entity.getEntityName();
                        long attributeTypeId = attributeType.getId();

                        Map<String, Object> params = new HashMap<>();
                        params.put("referenceEntity", referenceEntity);
                        params.put("objectId", objectId);
                        params.put("attributeTypeId", attributeTypeId);

                        Object result = sqlSession().selectOne(NS + ".referenceExistCheck", params);

                        if (result != null) {
                            throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_error", locale));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] getDescriptionRoles() {
        return getEditRoles();
    }

    @Override
    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        super.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sequenceBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        stringCultureBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        entityBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        stringLocaleBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sessionBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        permissionBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        logBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
