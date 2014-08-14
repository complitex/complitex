package org.complitex.pspoffice.person.report.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.complitex.pspoffice.person.report.download.RegistrationCardDownload;
import org.complitex.pspoffice.person.report.entity.RegistrationCard;
import org.complitex.pspoffice.person.report.service.RegistrationCardBean;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.complitex.pspoffice.report.web.ReportDownloadPanel;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.component.toolbar.PrintButton;
import org.complitex.template.web.component.toolbar.SaveButton;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Collection;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.feedback.FeedbackMessage.ERROR;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class RegistrationCardPage extends WebPage {

    private final Logger log = LoggerFactory.getLogger(RegistrationCardPage.class);
    @EJB
    private RegistrationCardBean registrationCardBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        private MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", RegistrationCardPage.this);
            this.messages = messages;
            add(new FeedbackPanel("messages"));
        }

        @Override
        protected void onBeforeRender() {
            super.onBeforeRender();
            for (FeedbackMessage message : messages) {
                getSession().getFeedbackMessages().add(message);
            }
        }
    }

    private class ReportFragment extends Fragment {

        private ReportFragment(String id, final RegistrationCard card) {
            super(id, "report", RegistrationCardPage.this);
            Registration registration = card.getRegistration();
            Person person = registration.getPerson();
            add(new Label("label", new StringResourceModel("label", null,
                    new Object[]{personStrategy.displayDomainObject(person, getLocale())})));

            final Locale systemLocale = localeBean.getSystemLocale();
            add(new Label("lastName", person.getLastName(getLocale(), systemLocale)));
            add(new Label("firstName", person.getFirstName(getLocale(), systemLocale)));
            add(new Label("middleName", person.getMiddleName(getLocale(), systemLocale)));
            add(new Label("nationality", card.getNationality()));
            add(new Label("birthDate", format(person.getBirthDate())));
            add(new Label("birthRegion", person.getBirthRegion()));
            add(new Label("birthDistrict", person.getBirthDistrict()));
            add(new Label("birthCity", person.getBirthCity()));
            add(new Label("arrivalRegion", registration.getArrivalRegion()));
            add(new Label("arrivalStreet", registration.getArrivalStreet()));
            add(new Label("arrivalDistrict", registration.getArrivalDistrict()));
            add(new Label("arrivalBuilding", registration.getArrivalBuildingNumber()));
            add(new Label("arrivalCorp", registration.getArrivalBuildingCorp()));
            add(new Label("arrivalApartment", registration.getArrivalApartment()));
            add(new Label("arrivalCity", registration.getArrivalCity()));
            add(new Label("arrivalDate", registration.getArrivalDate() != null ? format(registration.getArrivalDate()) : null));
            add(new Label("passportSeries", card.getPassportSeries()));
            add(new Label("passportNumber", card.getPassportNumber()));
            add(new Label("passportIssued", card.getPassportIssued()));
            add(new Label("address", addressRendererBean.displayAddress(card.getAddressEntity(), card.getAddressId(), getLocale())));
            add(new ListView<Person>("children", person.getChildren()) {

                @Override
                protected void populateItem(ListItem<Person> item) {
                    Person child = item.getModelObject();
                    item.add(new Label("child", personStrategy.displayDomainObject(child, getLocale()) + ", "
                            + format(child.getBirthDate()) + getString("children_birth_date_suffix")));
                }
            });
            final DomainObject militaryServiceRelation = person.getMilitaryServiceRelation();
            add(new Label("military", militaryServiceRelation != null
                    ? militaryServiceRelationStrategy.displayDomainObject(militaryServiceRelation, getLocale()) : null));
            add(new Label("registrationDate", format(registration.getRegistrationDate())));
            add(new Label("registrationType", registrationTypeStrategy.displayDomainObject(registration.getRegistrationType(), getLocale())));
            add(new Label("departureRegion", registration.getDepartureRegion()));
            add(new Label("departureDistrict", registration.getDepartureDistrict()));
            add(new Label("departureCity", registration.getDepartureCity()));
            add(new Label("departureDate", registration.getDepartureDate() != null ? format(registration.getDepartureDate()) : null));
            add(new Label("departureReason", registration.getDepartureReason()));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(WebCommonResourceInitializer.STYLE_CSS));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(
                RegistrationCardPage.class, RegistrationCardPage.class.getSimpleName() + ".css")));
    }

    public RegistrationCardPage(Registration registration, String addressEntity, long addressId) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        RegistrationCard card = null;
        try {
            card = registrationCardBean.get(registration, addressEntity, addressId);
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(card == null ? new MessagesFragment("content", messages) : new ReportFragment("content", card));

        //Загрузка отчетов
        final ReportDownloadPanel saveReportDownload = new ReportDownloadPanel("saveReportDownload", getString("report_download"),
                new RegistrationCardDownload(card), false);
        saveReportDownload.setVisible(card != null);
        add(saveReportDownload);

        final ReportDownloadPanel printReportDownload = new ReportDownloadPanel("printReportDownload", getString("report_download"),
                new RegistrationCardDownload(card), true);
        printReportDownload.setVisible(card != null);
        add(printReportDownload);

        SaveButton saveReportButton = new SaveButton("saveReportButton", true) {

            @Override
            protected void onClick(AjaxRequestTarget target) {
                saveReportDownload.open(target);
            }
        };
        saveReportButton.setVisible(saveReportDownload.isVisible());
        add(saveReportButton);

        PrintButton printReportButton = new PrintButton("printReportButton", true) {

            @Override
            protected void onClick(AjaxRequestTarget target) {
                printReportDownload.open(target);
            }
        };
        printReportButton.setVisible(printReportDownload.isVisible());
        add(printReportButton);
    }
}
