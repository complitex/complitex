package ru.complitex.osznconnection.file.web;

import org.apache.wicket.Page;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.ITemplateLink;

import java.util.List;
import java.util.Locale;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.osznconnection.file.web.file_description.RequestFileDescriptionPage;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.07.2010 14:01:04
 *
 *   Меню администрирование
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class AdminTemplateMenu extends ru.complitex.admin.web.AdminTemplateMenu {

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = super.getTemplateLinks(locale);

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(ImportPage.class, locale, "title");
            }

            @Override
            public Class<? extends Page> getPage() {
                return ImportPage.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "ImportPage";
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(RequestFileDescriptionPage.class, locale, "menu_title");
            }

            @Override
            public Class<? extends Page> getPage() {
                return RequestFileDescriptionPage.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "RequestFileDescriptionPage";
            }
        });

        return links;
    }
}