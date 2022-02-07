package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.address.util.AddressRenderer;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.util.StringUtil;
import ru.complitex.osznconnection.file.entity.BenefitData;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeFileGroupBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;

import static ru.complitex.osznconnection.file.entity.RequestStatus.BENEFIT_OWNER_NOT_ASSOCIATED;
import static ru.complitex.osznconnection.file.entity.RequestStatus.PROCESSED;
import static ru.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF.*;

public class PrivilegeConnectPanel extends Panel {

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private PrivilegeFileGroupBean privilegeFileGroupBean;

    @EJB
    private RequestFileBean requestFileBean;

    private class BenefitDataModel extends AbstractReadOnlyModel<List<BenefitData>> {

        private List<BenefitData> benefitData;

        protected List<BenefitData> load() {
            List<BenefitData> data = null;
            try {
                Cursor<BenefitData> cursor = serviceProviderAdapter.getBenefitData(facilityServiceType.getUserOrganizationId(),
                        facilityServiceType.getAccountNumber(), facilityServiceType.getDate());

                if (cursor.getResultCode() != -1) {
                    data = cursor.getData();
                }
            } catch (Exception e) {
                error(getString("db_error"));
                LoggerFactory.getLogger(getClass()).error("", e);
            }

            return data;
        }

        @Override
        public List<BenefitData> getObject() {
            if (benefitData == null) {
                benefitData = load();
            }
            return benefitData;
        }

        public void clear() {
            benefitData = null;
        }
    }
    private Dialog dialog;
    private FacilityServiceType facilityServiceType;
    private WebMarkupContainer container;
    private BenefitDataModel dataModel;

    public PrivilegeConnectPanel(String id, final Component... toUpdate) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(735);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        add(dialog);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(false);
        dialog.add(container);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        container.add(messages);

        Form form = new Form("form");
        container.add(form);

        container.add(new Label("name", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return facilityServiceType.getStringField(FacilityServiceTypeDBF.FIO);
            }
        }));

        container.add(new Label("address", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return AddressRenderer.displayAddress(null, facilityServiceType.getCity(), null,
                        facilityServiceType.getStreet(), facilityServiceType.getBuildingNumber(),
                        facilityServiceType.getBuildingCorp(), facilityServiceType.getApartment(), getLocale());
            }
        }));

        container.add(new Label("accountNumber", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return facilityServiceType.getAccountNumber();
            }
        }));

        container.add(new Label("inn", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return StringUtil.valueOf(facilityServiceType.getInn());
            }
        }));

        container.add(new Label("passport", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return facilityServiceType.getPassport();
            }
        }));

        dataModel = new BenefitDataModel();

        WebMarkupContainer table = new WebMarkupContainer("table") {

            @Override
            public boolean isVisible() {
                return dataModel.getObject() != null && !dataModel.getObject().isEmpty();
            }
        };
        table.setOutputMarkupPlaceholderTag(true);
        form.add(table);

        final IModel<BenefitData> benefitDataModel = new Model<BenefitData>();
        final RadioGroup<BenefitData> radioGroup = new RadioGroup<BenefitData>("radioGroup", benefitDataModel);
        radioGroup.setRequired(true);
        table.add(radioGroup);

        ListView<BenefitData> data = new ListView<BenefitData>("data", dataModel) {

            @Override
            protected void populateItem(ListItem<BenefitData> item) {
                BenefitData benefitData = item.getModelObject();

                item.add(new Radio<>("radio", item.getModel(), radioGroup).setEnabled(!benefitData.isEmpty()));
                item.add(new Label("firstName", StringUtil.valueOf(benefitData.getFirstName())));
                item.add(new Label("lastName", StringUtil.valueOf(benefitData.getLastName())));
                item.add(new Label("middleName", StringUtil.valueOf(benefitData.getMiddleName())));
                item.add(new Label("inn", StringUtil.valueOf(benefitData.getInn())));
                item.add(new Label("passport", StringUtil.valueOf(benefitData.getPassportSerial()) + " "
                        + StringUtil.valueOf(benefitData.getPassportNumber())));
                item.add(new Label("orderFamily", benefitData.getOrderFamily()));
                item.add(new Label("code", StringUtil.valueOf(benefitData.getCode())));
                item.add(new Label("userCount", StringUtil.valueOf(benefitData.getUserCount())));
            }
        };
        radioGroup.add(data);

        IndicatingAjaxButton connect = new IndicatingAjaxButton("connect") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                BenefitData selectedBenefitData = benefitDataModel.getObject();

                try {
                    if ("Ф".equals(selectedBenefitData.getBudget())) {
                        facilityServiceType.putUpdateField(KAT, selectedBenefitData.getCode());
                    }

                    if (selectedBenefitData.getDateIn() != null) {
                        facilityServiceType.putUpdateField(YEARIN, DateUtil.getYear(selectedBenefitData.getDateIn()));
                        facilityServiceType.putUpdateField(MONTHIN, DateUtil.getMonth(selectedBenefitData.getDateIn()) + 1);
                    }

                    if (selectedBenefitData.getDateOut() != null) {
                        facilityServiceType.putUpdateField(YEAROUT, DateUtil.getYear(selectedBenefitData.getDateOut()));
                        facilityServiceType.putUpdateField(MONTHOUT, DateUtil.getMonth(selectedBenefitData.getDateOut()) + 1);
                    }

                    facilityServiceType.putUpdateField(RAH, facilityServiceType.getAccountNumber());

                    if (facilityServiceType.getStatus().equals(BENEFIT_OWNER_NOT_ASSOCIATED)) {
                        facilityServiceType.setStatus(PROCESSED);
                    }

                    facilityServiceTypeBean.update(facilityServiceType);


                    PrivilegeFileGroup group = privilegeFileGroupBean.getPrivilegeFileGroup(facilityServiceType.getGroupId());

                    RequestFile dwellingCharacteristicsRequestFile = group.getDwellingCharacteristicsRequestFile();
                    RequestFile facilityServiceTypeRequestFile = group.getFacilityServiceTypeRequestFile();

                    if (dwellingCharacteristicsRequestFile != null) {
                        if (dwellingCharacteristicsBean.isDwellingCharacteristicsFileFilled(dwellingCharacteristicsRequestFile.getId())) {
                            dwellingCharacteristicsRequestFile.setStatus(RequestFileStatus.FILLED);
                            requestFileBean.save(dwellingCharacteristicsRequestFile);
                        }
                    }

                    if (facilityServiceTypeRequestFile != null){
                        if (facilityServiceTypeBean.isFacilityServiceTypeFileFilled(facilityServiceTypeRequestFile.getId())) {
                            facilityServiceTypeRequestFile.setStatus(RequestFileStatus.FILLED);
                            requestFileBean.save(facilityServiceTypeRequestFile);
                        }
                    }

                    if (toUpdate != null) {
                        for (Component component : toUpdate) {
                            target.add(component);
                        }
                    }
                    closeDialog(target);
                } catch (Exception e) {
                    error(getString("db_error"));
                    LoggerFactory.getLogger(getClass()).error("", e);
                }

                target.add(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }

            @Override
            public boolean isVisible() {
                return dataModel.getObject() != null && !dataModel.getObject().isEmpty();
            }
        };
        connect.setOutputMarkupPlaceholderTag(true);
        form.add(connect);

        AjaxLink cancel = new AjaxLink("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                closeDialog(target);
            }
        };
        form.add(cancel);
    }

    protected void closeDialog(AjaxRequestTarget target) {
        container.setVisible(false);
        facilityServiceType = null;
        target.add(container);
        dialog.close(target);
    }

    public void open(AjaxRequestTarget target, FacilityServiceType facilityServiceType) {
        this.facilityServiceType = facilityServiceType;

        container.setVisible(true);
        dataModel.clear();
        target.add(container);
        dialog.open(target);
    }
}
