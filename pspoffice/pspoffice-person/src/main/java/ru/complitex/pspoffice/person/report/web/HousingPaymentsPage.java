package ru.complitex.pspoffice.person.report.web;

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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.address.service.AddressRendererBean;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import ru.complitex.pspoffice.person.report.download.HousingPaymentsDownload;
import ru.complitex.pspoffice.person.report.entity.FamilyMember;
import ru.complitex.pspoffice.person.report.entity.HousingPayments;
import ru.complitex.pspoffice.person.report.service.HousingPaymentsBean;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.report.web.ReportDownloadPanel;
import ru.complitex.resources.WebCommonResourceInitializer;
import ru.complitex.template.web.component.toolbar.PrintButton;
import ru.complitex.template.web.component.toolbar.SaveButton;
import ru.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.feedback.FeedbackMessage.ERROR;
import static ru.complitex.common.util.StringUtil.valueOf;
import static ru.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class HousingPaymentsPage extends WebPage {

    private final Logger log = LoggerFactory.getLogger(HousingPaymentsPage.class);
    @EJB
    private HousingPaymentsBean housingPaymentsBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        private MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", HousingPaymentsPage.this);
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

        private ReportFragment(String id, final HousingPayments payments) {
            super(id, "report", HousingPaymentsPage.this);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("headInfo", new StringResourceModel("headInfo", null, Model.of(new Object[]{
                    personStrategy.displayDomainObject(payments.getOwner(), getLocale()),
                    payments.getPersonalAccount(),
                    addressRendererBean.displayAddress(payments.getAddressEntity(), payments.getAddressId(), getLocale())
            }))));
            ListView<FamilyMember> familyMembers = new ListView<FamilyMember>("familyMembers", payments.getFamilyMembers()) {

                @Override
                protected void populateItem(ListItem<FamilyMember> item) {
                    item.add(new Label("familyMemberNumber", String.valueOf(item.getIndex() + 1)));
                    final FamilyMember member = item.getModelObject();
                    item.add(new Label("familyMemberName", personStrategy.displayDomainObject(member.getPerson(), getLocale())));
                    item.add(new Label("familyMemberRelation", member.getRelation() != null
                            ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), getLocale())
                            : null));
                    item.add(new Label("familyMemberBirthDate", format(member.getPerson().getBirthDate())));
                    item.add(new Label("familyMemberPassport", member.getPassport()));
                }
            };
            add(familyMembers);
            add(new Label("total", new StringResourceModel("total", null, Model.of(new Object[]{payments.getFamilyMembers().size()}))));

            add(new Label("formOfOwnership", new StringResourceModel("formOfOwnership", null, Model.of(new Object[]{
                        valueOf(ownershipFormStrategy.displayDomainObject(payments.getOwnershipForm(), getLocale()))
                    }))));
            add(new Label("floorsInfo", new StringResourceModel("floorsInfo", null, Model.of(new Object[]{
                        valueOf(payments.getFloors())
                    }))));
            add(new Label("leftInfo", new StringResourceModel("leftInfo", null, Model.of(new Object[]{
                        valueOf(payments.getLift())
                    }))));
            add(new Label("hostelInfo", new StringResourceModel("hostelInfo", null, Model.of(new Object[]{
                        valueOf(payments.getHostel())
                    }))));
            add(new Label("roomsInfo", new StringResourceModel("roomsInfo", null, Model.of(new Object[]{
                        valueOf(payments.getRooms())
                    }))));
            add(new Label("floorInfo", new StringResourceModel("floorInfo", null, Model.of(new Object[]{
                        valueOf(payments.getFloor())
                    }))));
            add(new Label("stoveType", new StringResourceModel("stoveType", null, Model.of(new Object[]{
                        valueOf(payments.getStoveType())
                    }))));
            add(new Label("areaInfo", new StringResourceModel("areaInfo", null, Model.of(new Object[]{
                        valueOf(payments.getApartmentArea()), valueOf(payments.getBalconyArea()),
                        valueOf(payments.getNormativeArea())
                    }))));
            add(new Label("benefits", new StringResourceModel("benefits", null, Model.of(new Object[]{
                        valueOf(payments.getBenefits()), valueOf(payments.getBenefitPersons())
                    }))));
            add(new Label("paymentsAdjustedForBenefits",
                    new StringResourceModel("paymentsAdjustedForBenefits", null, Model.of(new Object[]{
                        valueOf(payments.getPaymentsAdjustedForBenefits())
                    }))));
            add(new Label("paymentsInfo", new StringResourceModel("paymentsInfo", null, Model.of(new Object[]{
                        valueOf(payments.getNormativePayments()),
                        valueOf(payments.getApartmentPayments()), valueOf(payments.getApartmentTariff()),
                        valueOf(payments.getHeatPayments()), valueOf(payments.getHeatTariff()),
                        valueOf(payments.getGasPayments()), valueOf(payments.getGasTariff()),
                        valueOf(payments.getColdWaterPayments()), valueOf(payments.getColdWaterTariff()),
                        valueOf(payments.getHotWaterPayments()), valueOf(payments.getHotWaterTariff()),
                        valueOf(payments.getOutletPayments()), valueOf(payments.getOutletTariff())
                    }))));
            add(new Label("debt", new StringResourceModel("debt", null, Model.of(new Object[]{
                        valueOf(payments.getDebt()), valueOf(payments.getDebtMonth())
                    }))));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(WebCommonResourceInitializer.STYLE_CSS));
    }

    public HousingPaymentsPage(ApartmentCard apartmentCard) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        HousingPayments payments = null;
        try {
            payments = housingPaymentsBean.get(apartmentCard);
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(payments == null ? new MessagesFragment("content", messages) : new ReportFragment("content", payments));

        //Загрузка отчетов
        final ReportDownloadPanel saveReportDownload = new ReportDownloadPanel("saveReportDownload", getString("report_download"),
                new HousingPaymentsDownload(payments), false);
        saveReportDownload.setVisible(payments != null);
        add(saveReportDownload);

        final ReportDownloadPanel printReportDownload = new ReportDownloadPanel("printReportDownload", getString("report_download"),
                new HousingPaymentsDownload(payments), true);
        printReportDownload.setVisible(payments != null);
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
