package ru.complitex.keconnection.web.admin;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.admin.web.AdminTemplateMenu;
import ru.complitex.template.web.template.ITemplateLink;

import java.util.List;
import java.util.Locale;
import ru.complitex.keconnection.importing.web.ImportPage;

/**
 * @author Artem
 */
public class AdminMenu extends AdminTemplateMenu {

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
                return "import";
            }
        });
        return links;
    }
}
