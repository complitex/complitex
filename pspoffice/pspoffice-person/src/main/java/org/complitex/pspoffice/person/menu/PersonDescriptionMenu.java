/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.menu;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.template.web.pages.EntityDescription;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_DESCRIPTION_EDIT)
public class PersonDescriptionMenu extends ResourceTemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return getString(MenuResources.class, locale, "person_description_menu");
    }

    private String getEntityName(String entity, Locale locale) {
        IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entity);
        return strategy.getEntity().getName(locale);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = ImmutableList.<ITemplateLink>of(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getEntityName("person", locale);
            }

            @Override
            public Class<? extends Page> getPage() {
                return EntityDescription.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters().set(EntityDescription.ENTITY, "person");
            }

            @Override
            public String getTagId() {
                return "person_description_item";
            }
        }, new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getEntityName("registration", locale);
            }

            @Override
            public Class<? extends Page> getPage() {
                return EntityDescription.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters().set(EntityDescription.ENTITY, "registration");
            }

            @Override
            public String getTagId() {
                return "registration_description_menu";
            }
        });
        return links;
    }

    @Override
    public String getTagId() {
        return "person_description_menu";
    }
}
