package org.complitex.osznconnection.service_provider_type.strategy;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Stateless
public class ServiceProviderTypeStrategy extends TemplateStrategy {

    /**
     * Attribute type ids
     */
    public static final long NAME = 1600;
    /**
     * Predefined service provider types ids:
     */
    public static final long APARTMENT_FEE = 1;
    public static final long HEATING = 2;
    public static final long HOT_WATER_SUPPLY = 3;
    public static final long COLD_WATER_SUPPLY = 4;
    public static final long GAS_SUPPLY = 5;
    public static final long POWER_SUPPLY = 6;
    public static final long GARBAGE_DISPOSAL = 7;
    public static final long DRAINAGE = 8;
    public static final Set<Long> RESERVED_SERVICE_PROVIDER_TYPES =
            ImmutableSet.of(APARTMENT_FEE, HEATING, HOT_WATER_SUPPLY, COLD_WATER_SUPPLY, GAS_SUPPLY, POWER_SUPPLY,
                    GARBAGE_DISPOSAL, DRAINAGE);

    private static final Map<Long, DomainObject> reservedServiceProviderTypeMap = new ConcurrentHashMap<Long, DomainObject>();

    @PostConstruct
    private void init() {
        if (reservedServiceProviderTypeMap.isEmpty()) {
            for (long serviceProviderTypeId : RESERVED_SERVICE_PROVIDER_TYPES) {
                DomainObject serviceProviderType = getDomainObject(serviceProviderTypeId, true);
                if (serviceProviderType != null) {
                    reservedServiceProviderTypeMap.put(serviceProviderTypeId, serviceProviderType);
                } else {
                    throw new IllegalStateException("Database does not contain reserved service provider. Service provider id: "
                            + serviceProviderTypeId);
                }
            }
        }
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends WebPage> getListPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PageParameters getListPageParams() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEntityName() {
        return "service_provider_type";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }


    @Override
    public DomainObject getDomainObject(Long id, boolean runAsAdmin) {
        if (runAsAdmin) {
            final DomainObject object = reservedServiceProviderTypeMap.get(id);
            if (object != null) {
                return object;
            }
        }
        return super.getDomainObject(id, runAsAdmin);
    }

    @Override
    public void configureExample(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                example.addAttributeFilter(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    public List<DomainObject> getAll(Locale locale) {
        List<DomainObject> all = Lists.newArrayList(reservedServiceProviderTypeMap.values());
        Collections.sort(all, new Comparator<DomainObject>() {

            @Override
            public int compare(DomainObject o1, DomainObject o2) {
                return o1.getObjectId().compareTo(o2.getObjectId());
            }
        });
        return all;
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }
}