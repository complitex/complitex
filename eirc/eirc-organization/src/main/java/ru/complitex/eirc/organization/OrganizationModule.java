package ru.complitex.eirc.organization;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.eirc.organization.strategy.EircOrganizationStrategy;
import ru.complitex.organization.DefaultOrganizationModule;
import ru.complitex.organization.IOrganizationModule;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = DefaultOrganizationModule.CUSTOM_ORGANIZATION_MODULE_BEAN_NAME)
@Startup
public class OrganizationModule implements IOrganizationModule {

    @EJB
    private EircOrganizationStrategy organizationStrategy;

    public static final String NAME = "ru.complitex.eirc.organization";

    @Override
    public Class<? extends WebPage> getEditPage() {
        return organizationStrategy.getEditPage();
    }

    @Override
    public PageParameters getEditPageParams() {
        return organizationStrategy.getEditPageParams(null, null, null);
    }
}
