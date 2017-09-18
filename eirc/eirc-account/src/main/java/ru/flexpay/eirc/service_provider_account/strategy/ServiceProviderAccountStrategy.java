package ru.flexpay.eirc.service_provider_account.strategy;

import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccountAttribute;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ServiceProviderAccountStrategy extends TemplateStrategy {
    public static final String SPA_NS = ServiceProviderAccountStrategy.class.getName();

    /**
     * Attribute ids
     */
    // Количество проживающих
    public static final long NUMBER_OF_HABITANTS = 6001L;
    // Площадь общая
    public static final long TOTAL_SQUARE = 6002L;
    // Площадь жилая
    public static final long LIVE_SQUARE = 6003L;
    // Площадь отапливаемая
    public static final long HEATED_SQUARE = 6004L;
    // Площадь отапливаемая
    public static final long RESPONSIBLE_PERSON = 6005L;
    // На закрытие
    public static final long TO_CLOSE = 6006L;

    @EJB
    private StringValueBean stringBean;

    @Override
    public Long getObjectId(String externalId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends DomainObject> getList(DomainObjectFilter example) {
        throw new UnsupportedOperationException("Use findById");
    }

    @Override
    public DomainObject getDomainObject(Long id, boolean runAsAdmin) {

        DomainObject object = new DomainObject(id);

        loadAttributes(object);
        fillAttributes(null, object);
        updateStringsForNewLocales(object);

        return object;
    }

    @Override
    public Long getCount(DomainObjectFilter example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(DomainObject object, Date insertDate) {
        for (Attribute attribute : object.getAttributes()) {
            attribute.setObjectId(object.getObjectId());
            attribute.setStartDate(insertDate);
            insertAttribute(attribute);
        }
    }

//    @Override
//    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
//
//        //attributes comparison
//        for (Attribute oldAttr : oldObject.getAttributes()) {
//            boolean removed = true;
//            for (Attribute newAttr : newObject.getAttributes()) {
//                if (oldAttr.getEntityAttributeId().equals(newAttr.getEntityAttributeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
//                    //the same attribute_type and the same attribute_id
//                    removed = false;
//                    boolean needToUpdateAttribute = false;
//
//                    EntityAttribute entityAttribute = getEntity().getAttribute(oldAttr.getEntityAttributeId());
//
//                    Long oldValueTypeId = oldAttr.getValueTypeId();
//                    Long newValueTypeId = newAttr.getValueTypeId();
//
//                    if (!Numbers.isEqual(oldValueTypeId, newValueTypeId)) {
//                        needToUpdateAttribute = true;
//                    } else {
//                        String attributeValueType = entityAttribute.getAttributeValueType(oldAttr.getValueTypeId()).getValueType();
//                        if (SimpleTypes.isSimpleType(attributeValueType)) {
//                            SimpleTypes simpleType = SimpleTypes.valueOf(attributeValueType.toUpperCase());
//                            switch (simpleType) {
//                                case STRING_VALUE: {
//                                    boolean valueChanged = false;
//                                    for (StringValue oldString : oldAttr.getStringValues()) {
//                                        for (StringValue newString : newAttr.getStringValues()) {
//                                            //compare strings
//                                            if (oldString.getLocaleId().equals(newString.getLocaleId())) {
//                                                if (!Strings.isEqual(oldString.getValue(), newString.getValue())) {
//                                                    valueChanged = true;
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    if (valueChanged) {
//                                        needToUpdateAttribute = true;
//                                    }
//                                }
//                                break;
//
//                                case GENDER:
//                                case BOOLEAN:
//                                case DATE:
//                                case DATE2:
//                                case MASKED_DATE:
//                                case DOUBLE:
//                                case INTEGER: {
//                                    String oldString = StringValueUtil.getSystemStringValue(oldAttr.getStringValues()).getValue();
//                                    String newString = StringValueUtil.getSystemStringValue(newAttr.getStringValues()).getValue();
//                                    if (!StringUtil.isEqualIgnoreCase(oldString, newString)) {
//                                        needToUpdateAttribute = true;
//                                    }
//                                }
//                                break;
//
//                                case BIG_STRING:
//                                case STRING: {
//                                    String oldString = StringValueUtil.getSystemStringValue(oldAttr.getStringValues()).getValue();
//                                    String newString = StringValueUtil.getSystemStringValue(newAttr.getStringValues()).getValue();
//                                    if (!Strings.isEqual(oldString, newString)) {
//                                        needToUpdateAttribute = true;
//                                    }
//                                }
//                                break;
//                            }
//                        } else {
//                            //reference object ids
//                            Long oldValueId = oldAttr.getValueId();
//                            Long newValueId = newAttr.getValueId();
//                            if (!Numbers.isEqual(oldValueId, newValueId)) {
//                                needToUpdateAttribute = true;
//                            }
//                        }
//                    }
//
//                    if (needToUpdateAttribute) {
//                        archiveAttribute(oldAttr, updateDate);
//                        newAttr.setStartDate(updateDate);
//                        newAttr.setObjectId(newObject.getObjectId());
//                        insertAttribute(newAttr);
//                    }
//                }
//            }
//            if (removed) {
//                archiveAttribute(oldAttr, updateDate);
//            }
//        }
//
//        for (Attribute newAttr : newObject.getAttributes()) {
//            boolean added = true;
//            for (Attribute oldAttr : oldObject.getAttributes()) {
//                if (oldAttr.getEntityAttributeId().equals(newAttr.getEntityAttributeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
//                    //the same attribute_type and the same attribute_id
//                    added = false;
//                    break;
//                }
//            }
//
//            if (added) {
//                newAttr.setStartDate(updateDate);
//                newAttr.setObjectId(newObject.getObjectId());
//                insertAttribute(newAttr);
//            }
//        }
//    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return "";
    }

    @Override
    public String getEntityName() {
        return "service_provider_account";
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.AUTHORIZED};
    }

    @Override
    protected Attribute getNewAttributeInstance() {
        return new ServiceProviderAccountAttribute();
    }

    @Override
    protected String getInsertAttributeStatement() {
        return SPA_NS + ".insertServiceProviderAccountAttribute";
    }

    @Override
    protected String getLoadAttributesStatement() {
        return SPA_NS + ".selectServiceProviderAccountAttribute";
    }

    public void deleteAttribute(ServiceProviderAccountAttribute attribute) {
        attribute.setEntityName(getEntityName());

        sqlSession().delete(SPA_NS + ".deleteServiceProviderAccountAttribute", attribute);
    }

    public void updateAttribute(ServiceProviderAccountAttribute attribute) {
        attribute.setEntityName(getEntityName());
        sqlSession().update(NS + ".updateServiceProviderAccountAttribute", attribute);
    }

    public ServiceProviderAccountAttribute findByPkId(Long pkId) {
        ServiceProviderAccountAttribute attribute =  new ServiceProviderAccountAttribute(pkId);
        attribute.setEntityName(getEntityName());

        return sqlSession().selectOne(SPA_NS + ".selectServiceProviderAccountAttributeByPkId", attribute);
    }
}
