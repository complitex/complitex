/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.housing_rights.strategy;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.Stateless;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.example.AttributeExample;
import org.complitex.common.entity.example.DomainObjectExample;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;
import static com.google.common.collect.Lists.*;
import static org.complitex.common.util.AttributeUtil.*;
import static org.complitex.common.util.ResourceUtil.*;
import static org.apache.wicket.util.string.Strings.*;

/**
 *
 * @author Artem
 */
@Stateless
public class HousingRightsStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = HousingRightsStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 3100;
    public static final long CODE = 3101;

    @Override
    public String getEntityTable() {
        return "housing_rights";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return newArrayList(NAME, CODE);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return getStringCultureValue(object, NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (isEmpty(searchTextInput)) {
            AttributeExample attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeExample(NAME);
                example.addAttributeExample(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_VIEW};
    }
}
