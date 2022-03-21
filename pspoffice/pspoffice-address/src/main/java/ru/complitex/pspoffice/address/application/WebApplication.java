package ru.complitex.pspoffice.address.application;

import de.agilecoders.wicket.core.Bootstrap;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.cdi.ConversationPropagation;
import org.apache.wicket.markup.html.WebPage;
import ru.complitex.address.page.*;
import ru.complitex.correction.page.*;
import ru.complitex.organization.page.OrganizationPage;
import ru.complitex.organization.page.OrganizationTypePage;
import ru.complitex.pspoffice.address.api.AddressJsonPage;
import ru.complitex.pspoffice.address.page.*;
import ru.complitex.pspoffice.address.sync.page.SyncPage;
import ru.complitex.user.page.LoginPage;
import ru.complitex.ui.page.BasePage;
import ru.complitex.user.session.WebSession;

/**
 * @author Ivanov Anatoliy
 */
public class WebApplication extends AuthenticatedWebApplication {
    public static final String APPLICATION = "pspoffice-address";

    @Override
    protected void init() {
        super.init();

        getCspSettings().blocking().disabled();

        new CdiConfiguration().setPropagation(ConversationPropagation.NONE).configure(this);

        Bootstrap.builder().install(this);

        mountPages();

        setMetaData(BasePage.NAME, "PSPOffice Address");

        setMetaData(BasePage.MENU, MenuPanel.class.getCanonicalName());
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return WebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    private void mountPages(){
        mountPage("login", LoginPage.class);
        mountPage("home", HomePage.class);

        mountPage("country", CountryPage.class);
        mountPage("region", RegionPage.class);
        mountPage("city-type", CityTypePage.class);
        mountPage("city", CityPage.class);
        mountPage("district", DistrictPage.class);
        mountPage("street-type", StreetTypePage.class);
        mountPage("street", StreetPage.class);
        mountPage("house", HousePage.class);
        mountPage("flat", FlatPage.class);
        mountPage("organization", OrganizationPage.class);
        mountPage("organization-type", OrganizationTypePage.class);

        mountPage("organization-correction", OrganizationCorrectionPage.class);
        mountPage("country-correction", CountryCorrectionPage.class);
        mountPage("region-correction", RegionCorrectionPage.class);
        mountPage("city-type-correction", CityTypeCorrectionPage.class);
        mountPage("city-correction", CityCorrectionPage.class);
        mountPage("district-correction", DistrictCorrectionPage.class);
        mountPage("street-type-correction", StreetTypeCorrectionPage.class);
        mountPage("street-correction", StreetCorrectionPage.class);
        mountPage("house-correction", HouseCorrectionPage.class);
        mountPage("flat-correction", FlatCorrectionPage.class);

        mountPage("synchronization", SyncPage.class);

        mountPage("api", AddressJsonPage.class);
    }
}
