/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.registration_type.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.exception.DeleteException;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.Stateless;
import java.util.*;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.collect.Lists.newArrayList;
import static ru.complitex.common.util.ResourceUtil.getString;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationTypeStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = RegistrationTypeStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 2600;
    public static final long PERMANENT = 1;
    private static final Set<Long> RESERVED_INSTANCE_IDS = of(PERMANENT);

    @Override
    public String getEntityName() {
        return "registration_type";
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter attrExample = filter.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                filter.addAttributeFilter(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityName(), locale);
    }

    @SuppressWarnings("unchecked")
    public List<DomainObject> getAll() {
        DomainObjectFilter example = new DomainObjectFilter();
        example.setOrderByAttributeTypeId(NAME);
        configureFilter(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) getList(example);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_VIEW};
    }


    @Override
    protected void deleteChecks(Long objectId, Locale locale) throws DeleteException {
        if (RESERVED_INSTANCE_IDS.contains(objectId)) {
            throw new DeleteException(getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }

    public Collection<StringValue> reservedNames() {
        final Collection<StringValue> reservedNames = newArrayList();

        for (long id : RESERVED_INSTANCE_IDS) {
            final DomainObject o = getDomainObject(id, true);
            if (o != null) {
                reservedNames.addAll(ImmutableList.copyOf(o.getAttribute(NAME).getStringValues()));
            }
        }

        return ImmutableList.copyOf(reservedNames);
    }
}
