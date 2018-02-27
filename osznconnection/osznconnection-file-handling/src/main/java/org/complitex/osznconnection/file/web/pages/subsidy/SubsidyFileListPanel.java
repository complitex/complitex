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
import org.complitex.common.converter.BigDecimalConverter;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessManagerService;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.complitex.osznconnection.file.entity.RequestFileType.SUBSIDY;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.12.13 18:51
 */
public class SubsidyFileListPanel extends AbstractFileListPanel {
    private BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(2);

    @EJB
    private ProcessManagerService processManagerService;

    @EJB
    private RequestFileBean requestFileBean;

    public SubsidyFileListPanel(String id) {
        super(id, SUBSIDY, LOAD_SUBSIDY, BIND_SUBSIDY, FILL_SUBSIDY, SAVE_SUBSIDY,
                new Long[]{OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE});

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
    protected Class<? extends WebPage> getItemListPageClass() {
        return SubsidyList.class;
    }

    @Override
    protected String getPreferencePage() {
        return SubsidyFileList.class.getName();
    }

    @Override
    protected ProcessType getExportProcessType() {
        return ProcessType.EXPORT_SUBSIDY_MASTER_DATA;
    }

    @Override
    protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        processManagerService.loadSubsidy(userOrganizationId, organizationId, year, monthFrom, monthTo);
    }

    @Override
    protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.bindSubsidy(selectedFileIds, parameters);
    }

    @Override
    protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.fillSubsidy(selectedFileIds, parameters);
    }

    @Override
    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.saveSubsidy(selectedFileIds, parameters);
    }

    @Override
    protected void export(AjaxRequestTarget target, List<Long> selectedFileIds) {
        processManagerService.exportSubsidy(selectedFileIds.stream()
                .map(requestFileBean::getRequestFile)
                .filter(rf -> rf.getStatus().equals(RequestFileStatus.FILLED) ||
                        rf.getStatus().equals(RequestFileStatus.FILL_ERROR))
                .map(RequestFile::getId)
                .collect(Collectors.toList()));
    }

    @Override
    protected boolean isExportVisible() {
        return true;
    }
}
