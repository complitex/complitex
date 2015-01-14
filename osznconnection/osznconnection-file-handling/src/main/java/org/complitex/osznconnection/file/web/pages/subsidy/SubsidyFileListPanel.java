package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.common.converter.BigDecimalConverter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.component.ajax.AjaxLinkPanel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.organization.OrganizationPicker;
import org.complitex.common.web.component.organization.OrganizationPickerDialog;
import org.complitex.correction.entity.OrganizationCorrection;
import org.complitex.correction.service.OrganizationCorrectionBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.service.SubsidyService;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.file.web.SubsidyFileList;
import org.complitex.osznconnection.file.web.component.load.DateParameter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.organization_type.strategy.OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.12.13 18:51
 */
public class SubsidyFileListPanel extends AbstractFileListPanel {
    private BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(2);

    @EJB
    private ProcessManagerBean processManagerBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private SubsidyService subsidyService;

    @EJB
    private OrganizationCorrectionBean organizationCorrectionBean;

    @EJB
    private ModuleBean moduleBean;

    private OrganizationPickerDialog organizationPickerDialog;
    private Model<RequestFile> selectedRequestFileModel = Model.of(new RequestFile());

    private SubsidyExportDialog subsidyExportDialog;

    public SubsidyFileListPanel(String id) {
        super(id);

        add(organizationPickerDialog = new OrganizationPickerDialog("organization_correction_dialog",
                Model.of(new DomainObject()), SERVICING_ORGANIZATION_TYPE){
            @Override
            protected void onSelect(AjaxRequestTarget target) {
                RequestFile requestFile = selectedRequestFileModel.getObject();
                DomainObject organization = getOrganizationModel().getObject();

                OrganizationCorrection correction = new OrganizationCorrection(null, organization.getObjectId(),
                        requestFile.getCode(), requestFile.getOrganizationId(), requestFile.getUserOrganizationId(),
                        moduleBean.getModuleId());

                if (organizationCorrectionBean.getOrganizationCorrectionsCount(FilterWrapper.of(correction)) == 0){
                    organizationCorrectionBean.save(correction);

                    getSession().info(String.format(getString("info_correction_added"),
                            organizationStrategy.displayShortNameAndCode(organization, getLocale())));
                }else {
                    getSession().error(getString("error_correction_exist"));
                }

                target.add(getMessages(), getDataViewContainer());
            }
        });

        add(subsidyExportDialog = new SubsidyExportDialog("subsidy_export_dialog"){
            @Override
            protected void onExport(AjaxRequestTarget target) {
                SubsidyFileListPanel.this.startTimer(target, ProcessType.EXPORT_SUBSIDY);
            }
        });

        addColumn(new Column() {
            @Override
            public Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh) {
                return new ArrowOrderByBorder("header.servicing_organization", "servicing_organization", stateLocator,
                        dataView, refresh);
            }

            @Override
            public Component filter() {
                return new OrganizationPicker("servicingOrganization",
                        new PropertyModel<DomainObject>(getModel(), "servicingOrganization"),
                        SERVICING_ORGANIZATION_TYPE);
            }

            @Override
            public Component field(Item<RequestFile> item) {
                final RequestFile rf = item.getModelObject();
                final String code = rf.getName().substring(0, rf.getName().length() - 8);

                return new AjaxLinkPanel("servicing_organization", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        Long organizationId = subsidyService.getServicingOrganizationId(rf);

                        if (organizationId != null){
                            return organizationStrategy.displayShortNameAndCode(organizationStrategy.getDomainObject(organizationId, true),
                                    getLocale());
                        }else {
                            return code;
                        }
                    }
                }) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        organizationPickerDialog.open(target);
                        selectedRequestFileModel.setObject(rf);
                    }

                    @Override
                    public boolean isEnabled() {
                        return subsidyService.getServicingOrganizationId(rf) == null;
                    }
                };

            }
        });

        addColumn(new Column() {
                @Override
                public Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh) {
                    return new ArrowOrderByBorder("header.sum", "sum", stateLocator, dataView, refresh);
                }

                @Override
                public Component filter() {
                    return new TextField<>("sum");
                }

                @Override
                public Component field(final Item<RequestFile> item) {
                    return new Label("sum", new LoadableDetachableModel<String>(){

                        @Override
                        protected String load() {
                            return bigDecimalConverter.convertToString(item.getModelObject().getSum(), getLocale());
                        }
                    });
                }
        });
    }

    @Override
    protected String getPreferencePage() {
        return SubsidyFileList.class.getName();
    }

    @Override
    protected RequestFileType getRequestFileType() {
        return RequestFileType.SUBSIDY;
    }

    @Override
    protected Class<? extends WebPage> getItemListPageClass() {
        return SubsidyList.class;
    }

    @Override
    protected ProcessType getLoadProcessType() {
        return ProcessType.LOAD_SUBSIDY;
    }

    @Override
    protected ProcessType getBindProcessType() {
        return ProcessType.BIND_SUBSIDY;
    }

    @Override
    protected ProcessType getFillProcessType() {
        return ProcessType.FILL_SUBSIDY;
    }

    @Override
    protected ProcessType getSaveProcessType() {
        return ProcessType.SAVE_SUBSIDY;
    }

    @Override
    protected ProcessType getExportProcessType() {
        return ProcessType.EXPORT_SUBSIDY;
    }

    @Override
    protected void load(long userOrganizationId, long osznId, DateParameter dateParameter) {
        processManagerBean.loadSubsidy(userOrganizationId, osznId,
                dateParameter.getMonthFrom(), dateParameter.getMonthTo(), dateParameter.getYear());
    }

    @Override
    protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
        processManagerBean.bindSubsidy(selectedFileIds, commandParameters);
    }

    @Override
    protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
        processManagerBean.fillSubsidy(selectedFileIds, commandParameters);
    }

    @Override
    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
        processManagerBean.saveSubsidy(selectedFileIds, commandParameters);
    }

    @Override
    protected void export(AjaxRequestTarget target, List<Long> selectedFileIds) {
        subsidyExportDialog.open(target);
    }

    @Override
    protected boolean isExportVisible() {
        return true;
    }
}
