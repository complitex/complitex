/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.departure_reason.strategy;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;
import static com.google.common.collect.Lists.*;
import static org.complitex.dictionary.util.AttributeUtil.*;
import static org.complitex.dictionary.util.ResourceUtil.*;
import static org.apache.wicket.util.string.Strings.*;

/**
 *
 * @author Artem
 */
@Stateless
public class DepartureReasonStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = DepartureReasonStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 3000;
    public static final long CODE = 3001;

    @Override
    public String getEntityTable() {
        return "departure_reason";
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
