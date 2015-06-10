package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.common.web.component.datatable.column.RadioColumn;
import org.complitex.common.web.component.dateinput.MaskedDateInput;
import org.complitex.common.web.component.wiquery.ExtendedDialog;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeter;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeterCursor;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterServiceStub;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 05.06.2015 0:26.
 */
public class ComMeterDialog extends Panel {
    @EJB
    private ExternalHeatmeterServiceStub externalHeatmeterService;

    private ExtendedDialog dialog;
    private WebMarkupContainer container;
    private FilteredDataTable<ComMeter> dataTable;

    private IModel<ComMeterCursor> model;

    private CentralHeatingConsumption consumption;

    public ComMeterDialog(String id) {
        super(id);

        model = new CompoundPropertyModel<>(new ComMeterCursor());

        dialog = new ExtendedDialog("dialog");
        dialog.setWidth(900);
        add(dialog);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        dialog.add(container);

        Form form = new Form<>("form", model);
        container.add(form);

        form.add(new TextField("organizationCode"));
        form.add(new TextField<>("buildingCode"));
        form.add(new MaskedDateInput("om"));
        form.add(new TextField<>("serviceCode"));

        form.add(new AjaxButton("search", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                externalHeatmeterService.callComMeterCursor(model.getObject());

                target.add(container);
                dialog.center(target);
            }
        });

        dataTable = new FilteredDataTable<ComMeter>("dataTable", ComMeter.class, false, "radio", "mId", "mNum", "mDate", "mType") {
            @Override
            public List<ComMeter> getList(FilterWrapper<ComMeter> filterWrapper) {
                return model.getObject().getData();
            }

            @Override
            public Long getCount(FilterWrapper<ComMeter> filterWrapper) {
                return (long) model.getObject().getData().size();
            }

            @Override
            protected IColumn<ComMeter, String> onColumn(String field) {
                if (field.equals("radio")){
                    return new RadioColumn<>();
                }

                return super.onColumn(field);
            }
        };
        form.add(dataTable);

        form.add(new AjaxLink("select") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onSelect(target, consumption, dataTable.getRadioGroupModel().getObject());

                dialog.close(target);
            }
        });
        form.add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        });
    }

    public void open(AjaxRequestTarget target, CentralHeatingConsumption consumption, ComMeterCursor comMeterCursor){
        this.consumption = consumption;
        model.setObject(comMeterCursor);

        target.add(container);

        dialog.open(target);
    }

    protected void onSelect(AjaxRequestTarget target, CentralHeatingConsumption consumption, ComMeter comMeter){
    }
}
