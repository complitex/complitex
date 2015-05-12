package org.complitex.organization.strategy;

import org.complitex.common.entity.DomainObject;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.Stateless;
import java.util.Locale;

/**
 * @author Anatoly Ivanov
 *         Date: 12.11.2014 18:01
 */
@Stateless
public class ServiceStrategy extends TemplateStrategy{
    public final static long NAME = 3601;
    public final static long SHORT_NAME = 3602;
    public final static long CODE = 3603;

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public String getEntityName() {
        return "service";
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
