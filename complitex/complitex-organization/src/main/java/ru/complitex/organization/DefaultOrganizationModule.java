package ru.complitex.organization;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.strategy.TemplateStrategy;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "DefaultOrganizationModule")
@Startup
public class DefaultOrganizationModule implements IOrganizationModule {

    public static final String NAME = "ru.complitex.organization";
    public static final String CUSTOM_ORGANIZATION_MODULE_BEAN_NAME = "OrganizationModule";

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @PostConstruct
    public void init() {
        registerLink();
    }

    private void registerLink() {
        IOrganizationModule organizationModule = EjbBeanLocator.getBean(CUSTOM_ORGANIZATION_MODULE_BEAN_NAME, true);

        if (organizationModule == null) {
            organizationModule = this;
        }

        LogManager.get().registerLink(DomainObject.class.getName(), organizationStrategy.getEntityName(),
                organizationModule.getEditPage(),
                organizationModule.getEditPageParams(),
                TemplateStrategy.OBJECT_ID);
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return organizationStrategy.getEditPage();
    }

    @Override
    public PageParameters getEditPageParams() {
        return new PageParameters().set(TemplateStrategy.ENTITY, organizationStrategy.getEntityName());
    }
}
