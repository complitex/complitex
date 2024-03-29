package ru.complitex.osznconnection.file.web.pages.subsidy;

import com.google.common.collect.Lists;
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
import ru.complitex.common.util.StringUtil;
import ru.complitex.osznconnection.file.entity.*;
import ru.complitex.osznconnection.file.entity.subsidy.Benefit;
import ru.complitex.osznconnection.file.entity.subsidy.BenefitDBF;
import ru.complitex.osznconnection.file.service.StatusRenderUtil;
import ru.complitex.osznconnection.file.service.subsidy.BenefitBean;
import ru.complitex.osznconnection.file.service.warning.WebWarningRenderer;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Collection;
import java.util.List;

public class BenefitConnectPanel extends Panel {

    @EJB
    private BenefitBean benefitBean;

    @EJB
    private WebWarningRenderer webWarningRenderer;

    private class BenefitDataModel extends AbstractReadOnlyModel<List<BenefitData>> {

        private List<BenefitData> benefitData;

        protected List<BenefitData> load() {
            List<BenefitData> data = null;
            try {
                Collection<BenefitData> benefitDataCollection = benefitBean.getBenefitData(benefit);
                if (benefitDataCollection != null) {
                    data = Lists.newArrayList(benefitDataCollection);
                }
            } catch (Exception e) {
                error(getString("db_error"));
                LoggerFactory.getLogger(getClass()).error("", e);
            }

            switch (benefit.getStatus()) {
                case ACCOUNT_NUMBER_NOT_FOUND:
                case PROCESSING_INVALID_FORMAT:
                    error(StatusRenderUtil.displayStatus(benefit.getStatus(), getLocale()));
                    break;
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
    private Benefit benefit;
    private WebMarkupContainer container;
    private BenefitDataModel dataModel;

    public BenefitConnectPanel(String id, final Component... toUpdate) {
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
                return benefit.getStringField(BenefitDBF.SUR_NAM) + " "
                        + benefit.getStringField(BenefitDBF.F_NAM) + " "
                        + benefit.getStringField(BenefitDBF.M_NAM);
            }
        }));

        container.add(new Label("address", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return AddressRenderer.displayAddress(null, benefit.getCity(), null, benefit.getStreet(), benefit.getBuildingNumber(),
                        benefit.getBuildingCorp(), benefit.getApartment(), getLocale());
            }
        }));

        container.add(new Label("accountNumber", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return benefit.getAccountNumber();
            }
        }));

        container.add(new Label("inn", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return StringUtil.valueOf(benefit.getStringField(BenefitDBF.IND_COD));
            }
        }));

        container.add(new Label("passport", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return StringUtil.valueOf(benefit.getStringField(BenefitDBF.PSP_SER))
                        + StringUtil.valueOf(benefit.getStringField(BenefitDBF.PSP_NUM));
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

                item.add(new Radio<BenefitData>("radio", item.getModel(), radioGroup).setEnabled(!benefitData.isEmpty()));
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
                if (validateBenefitData(selectedBenefitData)) {
                    try {
                        benefitBean.connectBenefit(benefit, selectedBenefitData, dataModel.getObject().size() > 1);

                        switch (benefit.getStatus()) {
                            case ACCOUNT_NUMBER_NOT_FOUND:
                                error(StatusRenderUtil.displayStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND, getLocale()));
                                break;
                            case PROCESSING_INVALID_FORMAT:
                                error(StatusRenderUtil.displayStatus(RequestStatus.PROCESSING_INVALID_FORMAT, getLocale()));
                                break;
                            default:
                                if (toUpdate != null) {
                                    for (Component component : toUpdate) {
                                        target.add(component);
                                    }
                                }
                                closeDialog(target);
                                return;
                        }
                    } catch (Exception e) {
                        error(getString("db_error"));
                        LoggerFactory.getLogger(getClass()).error("", e);
                    }
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

    private boolean validateBenefitData(BenefitData benefitData) {
        boolean valid = true;
        long osznId = benefit.getOrganizationId();
        long calculationCenterId = benefitData.getBillingId();
        String osznBenefitCode = benefitData.getPrivilegeCode();

        if (osznBenefitCode == null) {
            RequestWarning warning = new RequestWarning(RequestFileType.BENEFIT, RequestWarningStatus.PRIVILEGE_CODE_NOT_FOUND);
            warning.addParameter(new RequestWarningParameter(0, "privilege", osznBenefitCode));
            warning.addParameter(new RequestWarningParameter(1, "organization", osznId));
            error(webWarningRenderer.display(warning, getLocale()));
            valid = false;
        } else {
            try {
                Integer.valueOf(osznBenefitCode);
            } catch (NumberFormatException e) {
                RequestWarning warning = new RequestWarning(RequestFileType.BENEFIT, RequestWarningStatus.PRIVILEGE_CODE_INVALID);
                warning.addParameter(new RequestWarningParameter(0, osznBenefitCode));
                warning.addParameter(new RequestWarningParameter(1, "organization", osznId));
                warning.addParameter(new RequestWarningParameter(2, "privilege", osznBenefitCode));
                error(webWarningRenderer.display(warning, getLocale()));
                valid = false;
            }

            try {
                Integer.valueOf(benefitData.getOrderFamily());
            } catch (NumberFormatException e) {
                RequestWarning warning = new RequestWarning(RequestFileType.BENEFIT, RequestWarningStatus.ORD_FAM_INVALID);
                warning.addParameter(new RequestWarningParameter(0, benefitData.getOrderFamily()));
                warning.addParameter(new RequestWarningParameter(1, "organization", calculationCenterId));
                error(webWarningRenderer.display(warning, getLocale()));
                valid = false;
            }
        }

        return valid;
    }

    protected void closeDialog(AjaxRequestTarget target) {
        container.setVisible(false);
        benefit = null;
        target.add(container);
        dialog.close(target);
    }

    public void open(AjaxRequestTarget target, Benefit benefit) {
        this.benefit = benefit;

        container.setVisible(true);
        dataModel.clear();
        target.add(container);
        dialog.open(target);
    }
}
