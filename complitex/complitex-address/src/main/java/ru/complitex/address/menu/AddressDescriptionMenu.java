package ru.complitex.address.menu;

import com.google.common.collect.Lists;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.AddressInfoProvider;
import ru.complitex.address.resource.CommonResources;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.web.pages.EntityDescription;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.ITemplateLink;
import ru.complitex.template.web.template.ResourceTemplateMenu;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADDRESS_MODULE_EDIT)
public class AddressDescriptionMenu extends ResourceTemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return getString(CommonResources.class, locale, "address_description_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = Lists.newArrayList();
        for (final String address : getAddressInfoProvider().getAddressInfo().getAddressDescriptions()) {
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(address).getEntity().getName(locale);
                }

                @Override
                public Class<? extends Page> getPage() {
                    return EntityDescription.class;
                }

                @Override
                public PageParameters getParameters() {
                    return new PageParameters().set(EntityDescription.ENTITY, address);
                }

                @Override
                public String getTagId() {
                    return address + "_address_description_item";
                }
            });
        }
        return links;
    }

    @Override
    public String getTagId() {
        return "address_description_menu";
    }

    private static AddressInfoProvider getAddressInfoProvider() {
        return EjbBeanLocator.getBean(AddressInfoProvider.class);
    }
}
