/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.menu;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import ru.complitex.template.web.template.ITemplateLink;
import ru.complitex.template.web.template.ResourceTemplateMenu;

import java.util.List;
import java.util.Locale;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_VIEW)
public class PersonMenu extends ResourceTemplateMenu {

    public static final String PERSON_MENU_ITEM = "person_item";

    private static IStrategy getPersonStrategy() {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("person");
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(MenuResources.class, locale, "person_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = ImmutableList.<ITemplateLink>of(
                new ITemplateLink() {

                    @Override
                    public String getLabel(Locale locale) {
                        return getPersonStrategy().getPluralEntityLabel(locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return getPersonStrategy().getListPage();
                    }

                    @Override
                    public PageParameters getParameters() {
                        return getPersonStrategy().getListPageParams();
                    }

                    @Override
                    public String getTagId() {
                        return PERSON_MENU_ITEM;
                    }
                });
        return links;
    }

    @Override
    public String getTagId() {
        return "person_menu";
    }
}
