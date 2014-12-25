/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.tarif.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.domain.validate.IValidator;
import org.complitex.keconnection.tarif.strategy.web.edit.TarifGroupValidator;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Artem
 */
@Stateless
public class TarifGroupStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = TarifGroupStrategy.class.getName();
    private static final String MAPPING_NAMESPACE = TarifGroupStrategy.class.getPackage().getName() + ".TarifGroup";
    /**
     * Attribute type ids
     */
    public static final long NAME = 3200;
    public static final long CODE = 3201;
    /**
     * Predefined tarif group ids
     */
    public static final long PEOPLE_TARIF_GROUP_ID = 1;
    public static final long ORGANIZATION_TARIF_GROUP_ID = 2;
    public static final long OTHER_TARIF_GROUP_ID = 3;
    /* Reserved tarif groups */
    private static final Set<Long> RESERVED_TARIF_GROUP_IDS = ImmutableSet.of(PEOPLE_TARIF_GROUP_ID,
            ORGANIZATION_TARIF_GROUP_ID, OTHER_TARIF_GROUP_ID);

    @Override
    public String getEntityTable() {
        return "tarif_group";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return Lists.newArrayList(NAME, CODE);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                example.addAttributeExample(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    public List<DomainObject> getAll() {
        DomainObjectFilter example = new DomainObjectFilter();
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) getList(example);
    }

    public DomainObject getObjectByCode(int code) {
        DomainObjectFilter example = new DomainObjectFilter();
        AttributeFilter codeExample = new AttributeFilter(CODE);
        codeExample.setValue(String.valueOf(code));
        example.addAttributeExample(codeExample);
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        List<? extends DomainObject> results = getList(example);
        if (results == null || results.isEmpty()) {
            return null;
        } else if (results.size() > 1) {
            throw new IllegalStateException("More one tarif groups have the same code.");
        } else {
            return results.get(0);
        }
    }

    public Long getIdByCode(int code) {
        DomainObject object = getObjectByCode(code);
        return object != null ? object.getObjectId() : null;
    }


    @Override
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (RESERVED_TARIF_GROUP_IDS.contains(objectId)) {
            throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }


    public Long validateCode(Long id, String code) {
        Map<String, Object> params = ImmutableMap.<String, Object>of("codeAT", CODE, "code", code);
        List<Long> results = sqlSession().selectList(MAPPING_NAMESPACE + ".validateCode", params);
        for (Long result : results) {
            if (!result.equals(id)) {
                return result;
            }
        }
        return null;
    }

    @Override
    public IValidator getValidator() {
        return new TarifGroupValidator();
    }

    @Override
    protected void extendOrderBy(DomainObjectFilter example) {
        if (example.getOrderByAttributeTypeId() != null
                && example.getOrderByAttributeTypeId().equals(CODE)) {
            example.setOrderByNumber(true);
        }
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }
}
