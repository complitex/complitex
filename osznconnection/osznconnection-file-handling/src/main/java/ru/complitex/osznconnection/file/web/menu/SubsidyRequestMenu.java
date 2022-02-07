package ru.complitex.osznconnection.file.web.menu;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.osznconnection.file.web.pages.subsidy.*;
import ru.complitex.template.web.template.ITemplateLink;
import ru.complitex.template.web.template.ResourceTemplateMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.08.2010 17:31:55
 */
@AuthorizeInstantiation({"SUBSIDY_GROUP", "SUBSIDY_ACTUAL", "SUBSIDY_FILE"})
public class SubsidyRequestMenu extends ResourceTemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return getString(SubsidyRequestMenu.class, locale, "title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(SubsidyRequestMenu.class, locale, "request_file_group_list");
            }

            @Override
            public Class<? extends Page> getPage() {
                return GroupList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "RequestFileGroupList";
            }

            @Override
            public String[] getRoles() {
                return new String[]{"SUBSIDY_GROUP"};
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(SubsidyRequestMenu.class, locale, "actual_payment");
            }

            @Override
            public Class<? extends Page> getPage() {
                return ActualPaymentFileList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "ActualPaymentFileList";
            }

            @Override
            public String[] getRoles() {
                return new String[]{"SUBSIDY_ACTUAL"};
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(SubsidyRequestMenu.class, locale, "oschadbank_request");
            }

            @Override
            public Class<? extends Page> getPage() {
                return OschadbankRequestFileList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "OschadbankRequestFileList";
            }

            @Override
            public String[] getRoles() {
                return new String[]{"SUBSIDY_GROUP"};
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(SubsidyRequestMenu.class, locale, "oschadbank_response");
            }

            @Override
            public Class<? extends Page> getPage() {
                return OschadbankResponseFileList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "OschadbankResponseFileList";
            }

            @Override
            public String[] getRoles() {
                return new String[]{"SUBSIDY_GROUP"};
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(SubsidyRequestMenu.class, locale, "subsidy");
            }

            @Override
            public Class<? extends Page> getPage() {
                return SubsidyFileList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "SubsidyFileList";
            }

            @Override
            public String[] getRoles() {
                return new String[]{"SUBSIDY_FILE"};
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(SubsidyRequestMenu.class, locale, "subsidy_tarif_list");
            }

            @Override
            public Class<? extends Page> getPage() {
                return SubsidyTarifFileList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "SubsidyTarifList";
            }

            @Override
            public String[] getRoles() {
                return new String[]{"SUBSIDY_GROUP"};
            }
        });

        return links;
    }

    @Override
    public String getTagId() {
        return "subsidy_request_menu";
    }
}
