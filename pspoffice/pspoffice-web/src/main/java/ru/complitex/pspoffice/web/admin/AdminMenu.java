package ru.complitex.pspoffice.web.admin;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.admin.web.AdminTemplateMenu;
import ru.complitex.pspoffice.importing.legacy.web.LegacyDataImportPage;
import ru.complitex.pspoffice.importing.reference_data.web.ReferenceDataImportPage;
import ru.complitex.pspoffice.report.html.web.ReportList;
import ru.complitex.template.web.template.ITemplateLink;

import java.util.List;
import java.util.Locale;

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
                return getString(ReferenceDataImportPage.class, locale, "title");
            }

            @Override
            public Class<? extends Page> getPage() {
                return ReferenceDataImportPage.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "reference_data_import";
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(LegacyDataImportPage.class, locale, "title");
            }

            @Override
            public Class<? extends Page> getPage() {
                return LegacyDataImportPage.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "legacy_import";
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(ReportList.class, locale, "title");
            }

            @Override
            public Class<? extends Page> getPage() {
                return ReportList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "report_html";
            }
        });

        return links;
    }
}
