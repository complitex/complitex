package org.complitex.pspoffice.frontend.web;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.SingleThemeProvider;
import de.agilecoders.wicket.less.BootstrapLess;
import de.agilecoders.wicket.themes.markup.html.material_design.MaterialDesignTheme;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.complitex.pspoffice.frontend.web.address.AddressListPage;
import org.complitex.pspoffice.frontend.web.address.building.BuildingListPage;
import org.complitex.pspoffice.frontend.web.domain.DomainEditPage;
import org.complitex.pspoffice.frontend.web.domain.DomainListPage;
import org.complitex.pspoffice.frontend.web.entity.EntityListPage;
import org.complitex.pspoffice.frontend.web.entity.EntityPage;
import org.complitex.pspoffice.frontend.web.person.PersonListPage;
import org.complitex.pspoffice.frontend.web.person.PersonPage;

/**
 * @author Anatoly A. Ivanov
 * 26.06.2017 13:41
 */
public class PspOfficeFontendWebApplication extends WebApplication{

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected void init() {
        super.init();

        getDebugSettings().setAjaxDebugModeEnabled(false);
        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(new ServletRoleCheckingStrategy()));

        new CdiConfiguration().configure(this);

        configureBootstrap();
        configureMount();
    }

    private void configureBootstrap() {
        IBootstrapSettings settings = new BootstrapSettings();
        Bootstrap.builder().withBootstrapSettings(settings).install(this);

        settings.setThemeProvider(new SingleThemeProvider(new MaterialDesignTheme()));


        BootstrapLess.install(this);
    }

    private void configureMount(){
        mountPage("login", LoginPage.class);
        mountPage("person", PersonListPage.class);
        mountPage("person/${id}", PersonPage.class);
        mountPage("address/${entity}", AddressListPage.class);
        mountPage("address/building", BuildingListPage.class);
        mountPage("entity", EntityListPage.class);
        mountPage("entity/${id}", EntityPage.class);
        mountPage("domain/${entity}", DomainListPage.class);
        mountPage("domain/${entity}/${id}", DomainEditPage.class);
    }
}
