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
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.report.download.FamilyAndCommunalApartmentInfoDownload;
import org.complitex.pspoffice.person.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.report.service.FamilyAndCommunalApartmentInfoBean;
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
public final class FamilyAndCommunalApartmentInfoPage extends WebPage {

    private final Logger log = LoggerFactory.getLogger(FamilyAndCommunalApartmentInfoPage.class);
    @EJB
    private FamilyAndCommunalApartmentInfoBean familyAndCommunalApartmentInfoBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private StrategyFactory strategyFactory;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        private MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", FamilyAndCommunalApartmentInfoPage.this);
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

        private ReportFragment(String id, final FamilyAndCommunalApartmentInfo info) {
            super(id, "report", FamilyAndCommunalApartmentInfoPage.this);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("labelDetails", new StringResourceModel("labelDetails", null,
                    new Object[]{info.getNeighbourFamilies().size()})));
            add(new Label("addressInfo", new StringResourceModel("addressInfo", null,
                    new Object[]{addressRendererBean.displayAddress(info.getAddressEntity(), info.getAddressId(), getLocale()),
                        info.getNeighbourFamilies().size()})));

            ListView<NeighbourFamily> neighbourFamilies = new ListView<NeighbourFamily>("neighbourFamilies",
                    info.getNeighbourFamilies()) {

                @Override
                protected void populateItem(ListItem<NeighbourFamily> item) {
                    final NeighbourFamily neighbourFamily = item.getModelObject();
                    item.add(new Label("neighbourFamilyApartmentNumber",
                            neighbourFamily.getApartment() != null
                            ? strategyFactory.getStrategy("apartment").displayDomainObject(neighbourFamily.getApartment(), getLocale())
                            : ""));
                    item.add(new Label("neighbourFamilyName", personStrategy.displayDomainObject(neighbourFamily.getPerson(), getLocale())));
                    item.add(new Label("neighbourFamilyRoomsAndArea", neighbourFamily.getRoomsAndAreaInfo()));
                    item.add(new Label("neighbourFamilyOtherBuildingsAndArea", neighbourFamily.getOtherBuildingsAndAreaInfo()));
                    item.add(new Label("neighbourFamilyLoggiaPresenceAndArea", neighbourFamily.getLoggiaAndAreaInfo()));
                }
            };
            add(neighbourFamilies);

            add(new Label("apartmentInfo", new StringResourceModel("apartmentInfo", null, new Object[]{
                        valueOf(info.getKitchenArea()), valueOf(info.getBathroomArea()), valueOf(info.getToiletArea()),
                        valueOf(info.getHallArea()), valueOf(info.getOtherSpaceInfo())
                    })));
            add(new Label("sharedSpaceInfo", new StringResourceModel("sharedSpaceInfo", null, new Object[]{
                        valueOf(info.getSharedArea())
                    })));
            add(new Label("floorInfo", new StringResourceModel("floorInfo", null, new Object[]{
                        valueOf(info.getFloor()), valueOf(info.getNumberOfStoreys())
                    })));
            add(new Label("familyLabel", new StringResourceModel("familyLabel", null, new Object[]{
                        valueOf(personStrategy.displayDomainObject(info.getOwner(), getLocale()))
                    })));

            ListView<FamilyMember> familyMembers = new ListView<FamilyMember>("familyMembers", info.getFamilyMembers()) {

                @Override
                protected void populateItem(ListItem<FamilyMember> item) {
                    item.add(new Label("familyMemberNumber", String.valueOf(item.getIndex() + 1)));
                    final FamilyMember member = item.getModelObject();
                    item.add(new Label("familyMemberName", personStrategy.displayDomainObject(member.getPerson(), getLocale())));
                    item.add(new Label("familyMemberRelation", member.getRelation() != null
                            ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), getLocale())
                            : null));
                    item.add(new Label("familyMemberBirthDate", format(member.getPerson().getBirthDate())));
                    item.add(new Label("familyMemberRegistrationDate", format(member.getRegistrationDate())));
                }
            };
            add(familyMembers);

            add(new Label("apartmentStoreroomInfo", new StringResourceModel("apartmentStoreroomInfo", null, new Object[]{
                        valueOf(info.getStoreroomArea()), valueOf(info.getBarnArea())
                    })));
            add(new Label("otherBuildingsInfo", new StringResourceModel("otherBuildingsInfo", null, new Object[]{
                        valueOf(info.getOtherBuildings())
                    })));
            add(new Label("maintenanceInfo", new ResourceModel("maintenanceInfo")));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(WebCommonResourceInitializer.STYLE_CSS));
    }

    public FamilyAndCommunalApartmentInfoPage(ApartmentCard apartmentCard) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndCommunalApartmentInfo info = null;
        try {
            info = familyAndCommunalApartmentInfoBean.get(apartmentCard);
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(info == null ? new MessagesFragment("content", messages) : new ReportFragment("content", info));

        //Загрузка отчетов
        final ReportDownloadPanel saveReportDownload = new ReportDownloadPanel("saveReportDownload", getString("report_download"),
                new FamilyAndCommunalApartmentInfoDownload(info), false);
        saveReportDownload.setVisible(info != null);
        add(saveReportDownload);

        final ReportDownloadPanel printReportDownload = new ReportDownloadPanel("printReportDownload", getString("report_download"),
                new FamilyAndCommunalApartmentInfoDownload(info), true);
        printReportDownload.setVisible(info != null);
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
