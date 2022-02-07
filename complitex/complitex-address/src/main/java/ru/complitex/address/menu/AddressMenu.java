package ru.complitex.address.menu;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.AddressInfoProvider;
import ru.complitex.address.resource.CommonResources;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.ITemplateLink;
import ru.complitex.template.web.template.ResourceTemplateMenu;

import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADDRESS_MODULE_VIEW)
public class AddressMenu extends ResourceTemplateMenu {
    public static final String ADDRESS_MENU_ITEM_SUFFIX = "_address_item";

    public AddressMenu() {
        for (final String addressEntity : getAddressInfoProvider().getAddressInfo().getAddresses()) {
            add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return getStrategy(addressEntity).getPluralEntityLabel(locale);
                }

                @Override
                public Class<? extends Page> getPage() {
                    return getStrategy(addressEntity).getListPage();
                }

                @Override
                public PageParameters getParameters() {
                    return getStrategy(addressEntity).getListPageParams();
                }

                @Override
                public String getTagId() {
                    return addressEntity + ADDRESS_MENU_ITEM_SUFFIX;
                }
            });
        }
    }

    private IStrategy getStrategy(String entity) {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entity);
    }

    private AddressInfoProvider getAddressInfoProvider() {
        return EjbBeanLocator.getBean(AddressInfoProvider.class);
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(CommonResources.class, locale, "address_menu");
    }

    @Override
    public String getTagId() {
        return "address_menu";
    }
}
