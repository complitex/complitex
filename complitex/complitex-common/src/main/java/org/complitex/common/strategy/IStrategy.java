package org.complitex.common.strategy;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.History;
import org.complitex.common.entity.description.Entity;
import org.complitex.common.entity.description.EntityAttributeType;
import org.complitex.common.entity.example.DomainObjectExample;
import org.complitex.common.web.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.domain.validate.IValidator;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.common.web.component.search.SearchComponentState;

import java.util.*;

/**
 *
 * @author Artem
 */
public interface IStrategy {
    String ARCHIVE_ATTRIBUTES_OPERATION = "archiveAttributes";
    String ATTRIBUTE_NAMESPACE = "org.complitex.common.entity.Attribute";
    String COUNT_OPERATION = "count";
    String DOMAIN_OBJECT_NAMESPACE = "org.complitex.common.entity.DomainObject";
    String FIND_BY_ID_OPERATION = "findById";
    String FIND_HISTORY_ATTRIBUTES_OPERATION = "findHistoryAttributes";
    String FIND_HISTORY_OBJECT_OPERATION = "findHistoryObject";
    String FIND_OPERATION = "find";
    String FIND_PARENT_IN_SEARCH_COMPONENT_OPERATION = "findParentInSearchComponent";
    String HAS_HISTORY_OPERATION = "hasHistory";
    String INSERT_OPERATION = "insert";
    String UPDATE_OPERATION = "update";
    String FIND_CHILDREN_PERMISSION_INFO_OPERATION = "findChildrenPermissionInfo";
    String FIND_CHILDREN_ACTIVITY_INFO_OPERATION = "findChildrenActivityInfo";
    String UPDATE_CHILDREN_ACTIVITY_OPERATION = "updateChildrenActivity";
    String DELETE_OPERATION = "delete";
    
    void archive(DomainObject object, Date endDate);
    
    void archiveAttributes(Collection<Long> attributeTypeIds, Date endDate);

    void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput);
    
    Long getCount(DomainObjectExample example);

    List<? extends DomainObject> getList(DomainObjectExample example);

    void disable(DomainObject object);

    String displayDomainObject(DomainObject object, Locale locale);

    String displayDomainObject(Long objectId, Locale locale);

    String displayAttribute(Attribute attribute, Locale locale);

    void enable(DomainObject object);
    
    DomainObject findById(Long id, boolean runAsAdmin);
    
    DomainObject findById(String dataSource, Long id, boolean runAsAdmin);

    public Long getObjectId(String externalId);
    
    DomainObject findHistoryObject(long objectId, Date date);

    SimpleObjectInfo findParentInSearchComponent(long id, Date date);

    String getAttributeLabel(Attribute attribute, Locale locale);

    String[] getRealChildren();

    String[] getLogicalChildren();

    Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelBeforeClass();

    Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass();

    long getDefaultOrderByAttributeId();

    Class<? extends WebPage> getEditPage();

    PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity);

    Entity getEntity(String dataSource);

    Entity getEntity();

    String getEntityTable();
    
    List<History> getHistory(long objectId);
    
    TreeSet<Date> getHistoryDates(long objectId);

    Class<? extends WebPage> getHistoryPage();

    PageParameters getHistoryPageParams(long objectId);

    List<EntityAttributeType> getListColumns();
    
    long getDefaultSortAttributeTypeId();

    Class<? extends WebPage> getListPage();

    PageParameters getListPageParams();

    ISearchCallback getParentSearchCallback();

    List<String> getParentSearchFilters();

    String[] getParents();

    String getPluralEntityLabel(Locale locale);

    ISearchCallback getSearchCallback();

    SearchComponentState getSearchComponentStateForParent(Long parentId, String parentEntity, Date date);

    List<String> getSearchFilters();

    int getSearchTextFieldSize();
    
    boolean allowProceedNextSearchFilter();

    IValidator getValidator();
    
    void insert(DomainObject object, Date insertDate);

    boolean isSimpleAttribute(final Attribute attribute);

    boolean isSimpleAttributeType(EntityAttributeType entityAttributeType);

    DomainObject newInstance();
    
    Long performDefaultValidation(DomainObject object, Locale locale);
    
    void update(DomainObject oldObject, DomainObject newObject, Date updateDate);
    
    void updateAndPropagate(DomainObject oldObject, DomainObject newObject, Date updateDate);
    
    void replacePermissions(DomainObjectPermissionInfo objectPermissionInfo, Set<Long> subjectIds);

    boolean isNeedToChangePermission(Set<Long> oldSubjectIds, Set<Long> newSubjectIds);

    String[] getListRoles();

    String[] getEditRoles();
    
    void changePermissions(DomainObjectPermissionInfo objectPermissionInfo, Set<Long> addSubjectIds, Set<Long> removeSubjectIds);

    void changePermissionsInDistinctThread(long objectId, long permissionId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds);
    
    void changeChildrenActivity(long parentId, boolean enable);

    boolean canPropagatePermissions(DomainObject object);
    
    void delete(long objectId, Locale locale) throws DeleteException;

    String[] getDescriptionRoles();

    Page getObjectNotFoundPage();
}
