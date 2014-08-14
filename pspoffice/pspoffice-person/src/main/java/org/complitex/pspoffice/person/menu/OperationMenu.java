/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.menu;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class OperationMenu extends ResourceTemplateMenu {

    public static final String REGISTRATION_MENU_ITEM = "registration_item";

    @Override
    public String getTitle(Locale locale) {
        return getString(MenuResources.class, locale, "operation_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        return ImmutableList.<ITemplateLink>of(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(MenuResources.class, locale, "registration");
            }

            @Override
            public Class<? extends Page> getPage() {
                return ApartmentCardSearch.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return REGISTRATION_MENU_ITEM;
            }
        });
    }

    @Override
    public String getTagId() {
        return "operation_menu";
    }
}
