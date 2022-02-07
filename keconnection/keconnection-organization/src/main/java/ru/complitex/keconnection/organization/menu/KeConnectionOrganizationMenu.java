package ru.complitex.keconnection.organization.menu;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.organization.web.OrganizationMenu;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.ITemplateLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class KeConnectionOrganizationMenu extends OrganizationMenu {

    @Override
    protected IStrategy getStrategy() {
        return EjbBeanLocator.getBean(IOrganizationStrategy.BEAN_NAME);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> list = new ArrayList<>(super.getTemplateLinks(locale));

        list.add(new ITemplateLink() {
            @Override
            public String getLabel(Locale locale) {
                return getString("operating_month", locale);
            }

            @SuppressWarnings("unchecked")
            @Override

            public Class<? extends Page> getPage() {
                try {
                    return (Class<? extends Page>) getClass().getClassLoader()
                            .loadClass("ru.complitex.keconnection.heatmeter.web.OperatingMonthList");
                } catch (Exception e) {
                    //no error
                }

                return null;
            }

            @Override
            public PageParameters getParameters() {
                return null;
            }

            @Override
            public String getTagId() {
                return "organization_month";
            }
        });

        return list;
    }
}
