package ru.complitex.organization.strategy;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.entity.IEntityName;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.Stateless;
import java.util.Locale;
import java.util.Map;

/**
 * @author Anatoly Ivanov
 *         Date: 12.11.2014 18:01
 */
@Stateless
public class ServiceStrategy extends TemplateStrategy{
    public final static long NAME = 1601;
    public final static long SHORT_NAME = 1602;
    public final static long CODE = 1603;

    public static final long APARTMENT_FEE = 1;
    public static final long HEATING = 2;
    public static final long HOT_WATER_SUPPLY = 3;
    public static final long COLD_WATER_SUPPLY = 4;
    public static final long GAS_SUPPLY = 5;
    public static final long POWER_SUPPLY = 6;
    public static final long GARBAGE_DISPOSAL = 7;
    public static final long DRAINAGE = 8;

    public static IEntityName SERVICE_ENTITY = new IEntityName() {
        @Override
        public String getEntityName() {
            return "service";
        }
    };

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {

    }

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

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(ServiceStrategy.class.getName(), getEntityName(), locale);
    }
}
