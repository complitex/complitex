package ru.complitex.keconnection.organization;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import ru.complitex.organization.DefaultOrganizationModule;
import ru.complitex.organization.IOrganizationModule;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = DefaultOrganizationModule.CUSTOM_ORGANIZATION_MODULE_BEAN_NAME)
@Startup
public class KeConnectionOrganizationModule implements IOrganizationModule {

    @EJB
    private KeOrganizationStrategy keOrganizationStrategy;

    public static final String NAME = "ru.complitex.keconnection.organization";

    @Override
    public Class<? extends WebPage> getEditPage() {
        return keOrganizationStrategy.getEditPage();
    }

    @Override
    public PageParameters getEditPageParams() {
        return keOrganizationStrategy.getEditPageParams(null, null, null);
    }
}
