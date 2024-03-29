package ru.complitex.eirc.web.admin;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.web.ImportPage;
import ru.complitex.admin.web.AdminTemplateMenu;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.web.pages.ConfigEdit;
import ru.complitex.template.web.template.ITemplateLink;
import ru.complitex.eirc.dictionary.strategy.ModuleInstanceStrategy;
import ru.complitex.eirc.dictionary.web.admin.EircConfigEdit;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 * @author Artem
 */
public class AdminMenu extends AdminTemplateMenu {

    @EJB
    private ModuleInstanceStrategy moduleInstanceStrategy;

    private IStrategy getStrategy() {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("module_instance");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = super.getTemplateLinks(locale);

        for (ITemplateLink link : links) {
            if (link.getPage().equals(ConfigEdit.class)) {
                links.remove(link);
                links.add(new ITemplateLink() {

                    @Override
                    public String getLabel(Locale locale) {
                        return getString(ConfigEdit.class, locale, "title");
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return EircConfigEdit.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return new PageParameters();
                    }

                    @Override
                    public String getTagId() {
                        return "ConfigEdit";
                    }
                });
                break;
            }
        }

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
                return getString(AdminMenu.class, locale, "module_instance");
            }

            @Override
            public Class<? extends Page> getPage() {
                return getStrategy().getListPage();
            }

            @Override
            public PageParameters getParameters() {
                return getStrategy().getListPageParams();
            }

            @Override
            public String getTagId() {
                return "ModuleInstance";
            }
        });


        return links;
    }
}
