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
import org.complitex.address.service.AddressRendererBean;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.report.download.FamilyAndHousingPaymentsDownload;
import org.complitex.pspoffice.person.report.entity.FamilyAndHousingPayments;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.service.FamilyAndHousingPaymentsBean;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.report.web.ReportDownloadPanel;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.component.toolbar.PrintButton;
import org.complitex.template.web.component.toolbar.SaveButton;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.feedback.FeedbackMessage.ERROR;
import static org.complitex.dictionary.util.StringUtil.valueOf;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class FamilyAndHousingPaymentsPage extends WebPage {

    private final Logger log = LoggerFactory.getLogger(FamilyAndHousingPaymentsPage.class);
    @EJB
    private FamilyAndHousingPaymentsBean familyAndHousingPaymentsBean;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private PersonStrategy personStrategy;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        private MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", FamilyAndHousingPaymentsPage.this);
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

        private ReportFragment(String id, final FamilyAndHousingPayments payments) {
            super(id, "report", FamilyAndHousingPaymentsPage.this);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("labelDetails", new ResourceModel("labelDetails")));
            add(new Label("nameInfo", new StringResourceModel("nameInfo", null,
                    new Object[]{personStrategy.displayDomainObject(payments.getOwner(), getLocale())})));
            add(new Label("personalAccount", new StringResourceModel("personalAccount", null,
                    new Object[]{valueOf(payments.getPersonalAccount())})));
            add(new Label("addressInfo", new StringResourceModel("addressInfo", null,
                    new Object[]{addressRendererBean.displayAddress(payments.getAddressEntity(), payments.getAddressId(), getLocale())})));

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
            add(new Label("total", new StringResourceModel("total", null, new Object[]{payments.getFamilyMembers().size()})));

            add(new Label("formOfOwnership", new StringResourceModel("formOfOwnership", null, new Object[]{
                        valueOf(ownershipFormStrategy.displayDomainObject(payments.getOwnershipForm(), getLocale()))
                    })));
            add(new Label("stoveType", new StringResourceModel("stoveType", null, new Object[]{
                        valueOf(payments.getStoveType())
                    })));
            add(new Label("areaInfo", new StringResourceModel("areaInfo", null, new Object[]{
                        valueOf(payments.getApartmentArea()), valueOf(payments.getHeatedArea()),
                        valueOf(payments.getNormativeArea()), valueOf(payments.getRooms())
                    })));
            add(new Label("benefits", new StringResourceModel("benefits", null, new Object[]{
                        valueOf(payments.getBenefits())
                    })));
            add(new Label("paymentsAdjustedForBenefits",
                    new StringResourceModel("paymentsAdjustedForBenefits", null, new Object[]{
                        valueOf(payments.getPaymentsAdjustedForBenefits())
                    })));
            add(new Label("paymentsInfo", new StringResourceModel("paymentsInfo", null, new Object[]{
                        valueOf(payments.getNormativePayments()), valueOf(payments.getApartmentPayments()),
                        valueOf(payments.getHeatPayments()), valueOf(payments.getGasPayments()),
                        valueOf(payments.getColdWaterPayments()), valueOf(payments.getHotWaterPayments())
                    })));
            add(new Label("debt", new StringResourceModel("debt", null, new Object[]{
                        valueOf(payments.getDebt()), valueOf(payments.getDebtMonth())
                    })));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(WebCommonResourceInitializer.STYLE_CSS));
    }

    public FamilyAndHousingPaymentsPage(ApartmentCard apartmentCard) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndHousingPayments payments = null;
        try {
            payments = familyAndHousingPaymentsBean.get(apartmentCard);
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(payments == null ? new MessagesFragment("content", messages) : new ReportFragment("content", payments));

        //Загрузка отчетов
        final ReportDownloadPanel saveReportDownload = new ReportDownloadPanel("saveReportDownload",
                getString("report_download"), new FamilyAndHousingPaymentsDownload(payments), false);
        saveReportDownload.setVisible(payments != null);
        add(saveReportDownload);

        final ReportDownloadPanel printReportDownload = new ReportDownloadPanel("printReportDownload",
                getString("report_download"), new FamilyAndHousingPaymentsDownload(payments), true);
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
