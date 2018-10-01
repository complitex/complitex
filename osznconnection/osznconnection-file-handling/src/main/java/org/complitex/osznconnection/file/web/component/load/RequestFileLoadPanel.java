package org.complitex.osznconnection.file.web.component.load;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.service.SessionBean;
import org.complitex.common.web.component.YearDropDownChoice;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.template.web.template.TemplateSession;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;

import static org.complitex.organization_type.strategy.OrganizationTypeStrategy.SERVICE_PROVIDER_TYPE;
import static org.complitex.organization_type.strategy.OrganizationTypeStrategy.USER_ORGANIZATION_TYPE;
import static org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE;
import static org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE;

public abstract class RequestFileLoadPanel extends Panel {
    @EJB
    private SessionBean sessionBean;

    private final Dialog dialog;

    private static final String MONTH_COMPONENT_ID = "monthComponent";

    public enum MonthParameterViewMode { RANGE, EXACT, HIDDEN }

    private enum LoadPreferenceKey {LOAD}

    private static class LoadPreference{
        private Long serviceProviderId;
        private Long osznId;
        private Integer year;
        private Integer monthFrom;
        private Integer monthTo;

        public Long getServiceProviderId() {
            return serviceProviderId;
        }

        public void setServiceProviderId(Long serviceProviderId) {
            this.serviceProviderId = serviceProviderId;
        }

        public Long getOsznId() {
            return osznId;
        }

        public void setOsznId(Long osznId) {
            this.osznId = osznId;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonthFrom() {
            return monthFrom;
        }

        public void setMonthFrom(Integer monthFrom) {
            this.monthFrom = monthFrom;
        }

        public Integer getMonthTo() {
            return monthTo;
        }

        public void setMonthTo(Integer monthTo) {
            this.monthTo = monthTo;
        }
    }

    public RequestFileLoadPanel(String id, IModel<String> title, final MonthParameterViewMode monthParameterViewMode,
                                Long[] osznOrganizationTypes) {
        super(id);

        dialog = new Dialog("dialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        dialog.setModal(true);
        dialog.setMinHeight(100);
        dialog.setTitle(title);
        add(dialog);

        WebMarkupContainer content = new WebMarkupContainer("content");
        dialog.add(content);

        FeedbackPanel messages = new FeedbackPanel("messages", new ContainerFeedbackMessageFilter(content));
        messages.setOutputMarkupId(true);
        content.add(messages);

        //Форма
        Form form = new Form("form");
        content.add(form);

        //ПУ
        IModel<Long> serviceProviderModel = new Model<>();
        form.add(new OrganizationIdPicker("serviceProvider", serviceProviderModel, SERVICE_PROVIDER_TYPE));

        //ОСЗН
        IModel<Long> osznModel = new Model<>();
        form.add(new OrganizationIdPicker("oszn", osznModel, osznOrganizationTypes != null ? osznOrganizationTypes :
                new Long[]{SUBSIDY_DEPARTMENT_TYPE, PRIVILEGE_DEPARTMENT_TYPE}));

        //user organization
        final WebMarkupContainer userOrganizationContainer = new WebMarkupContainer("userOrganizationContainer");
        form.add(userOrganizationContainer);

        IModel<Long> userOrganizationModel = new Model<>();
        userOrganizationContainer.add(new OrganizationIdPicker("userOrganization", userOrganizationModel, USER_ORGANIZATION_TYPE));
        userOrganizationContainer.setVisible(sessionBean.getCurrentUserOrganizationId(getSession()) == null);

        IModel<Integer> yearModel = new Model<>();
        form.add(new YearDropDownChoice("year", yearModel).setRequired(true));

        WebMarkupContainer monthParameterContainer = new WebMarkupContainer("monthParameterContainer");
        monthParameterContainer.setVisible(monthParameterViewMode != MonthParameterViewMode.HIDDEN);
        form.add(monthParameterContainer);

        IModel<MonthRange> monthRangeModel = new Model<>();
        Component monthComponent;

        switch (monthParameterViewMode) {
            case RANGE:
                monthComponent = new MonthRangePanel(MONTH_COMPONENT_ID, monthRangeModel);
                break;
            case EXACT:
                IModel<Integer> monthPickerModel = new Model<Integer>() {

                    @Override
                    public void setObject(Integer month) {
                        if (month != null) {
                            monthRangeModel.setObject(new MonthRange(month));
                        } else {
                            monthRangeModel.setObject(null);
                        }
                    }

                    @Override
                    public Integer getObject() {
                        MonthRange monthRange = monthRangeModel.getObject();
                        return monthRange != null ? monthRange.getMonthFrom() : null;
                    }
                };
                monthComponent = new MonthPickerPanel(MONTH_COMPONENT_ID, monthPickerModel);
                break;
            case HIDDEN:
            default:
                monthComponent = new EmptyPanel(MONTH_COMPONENT_ID);
                break;
        }
        monthParameterContainer.add(monthComponent);

        if (getPreferencePage() != null){
            LoadPreference loadPreference = getSession().getPreferenceObject(getPreferencePage(), LoadPreferenceKey.LOAD,
                    new LoadPreference());

            serviceProviderModel.setObject(loadPreference.getServiceProviderId());
            osznModel.setObject(loadPreference.getOsznId());
            yearModel.setObject(loadPreference.getYear());
            monthRangeModel.setObject(new MonthRange(loadPreference.getMonthFrom(), loadPreference.getMonthTo()));
        }

        //Загрузить
        AjaxButton load = new AjaxButton("load", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Long mainUserOrganizationId = sessionBean.getCurrentUserOrganizationId(RequestFileLoadPanel.this.getSession());
                long currentUserOrganizationId = mainUserOrganizationId != null ? mainUserOrganizationId
                        : userOrganizationModel.getObject();

                int year = yearModel.getObject();

                int monthFrom = -1;
                int monthTo = -1;

                if (monthParameterViewMode != MonthParameterViewMode.HIDDEN) {
                    MonthRange monthRange = monthRangeModel.getObject();

                    monthFrom = monthRange.getMonthFrom();
                    monthTo = monthRange.getMonthTo();
                }

                if (getPreferencePage() != null){
                    LoadPreference loadPreference = (LoadPreference) RequestFileLoadPanel.this.getSession()
                            .getPreference(getPreferencePage(), LoadPreferenceKey.LOAD).getObject();

                    loadPreference.setServiceProviderId(serviceProviderModel.getObject());
                    loadPreference.setOsznId(osznModel.getObject());
                    loadPreference.setYear(year);
                    loadPreference.setMonthFrom(monthFrom);
                    loadPreference.setMonthTo(monthTo);
                }

                load(serviceProviderModel.getObject(), currentUserOrganizationId, osznModel.getObject(), year, monthFrom, monthTo, target);

                target.add(messages);
                dialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        form.add(load);

        //Отмена
        AjaxLink cancel = new AjaxLink("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
        form.add(cancel);
    }

    public void open(AjaxRequestTarget target) {
        dialog.open(target);
    }

    @Override
    public TemplateSession getSession() {
        return (TemplateSession) super.getSession();
    }

    protected abstract void load(Long serviceProviderId, Long userOrganizationId, Long organizationId,
                                 int year, int monthFrom, int monthTo, AjaxRequestTarget target);

    protected String getPreferencePage(){
        return null;
    }
}
