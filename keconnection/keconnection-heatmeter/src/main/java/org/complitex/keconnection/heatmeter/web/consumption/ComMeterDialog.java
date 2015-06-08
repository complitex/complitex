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
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeter;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeterCursor;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterServiceStub;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 05.06.2015 0:26.
 */
public class ComMeterDialog extends Panel {
    @EJB
    private ExternalHeatmeterServiceStub externalHeatmeterService;

    private Dialog dialog;
    private WebMarkupContainer container;
    private FilteredDataTable<ComMeter> dataTable;

    private IModel<ComMeterCursor> model;

    public ComMeterDialog(String id) {
        super(id);

        model = new CompoundPropertyModel<>(new ComMeterCursor());

        dialog = new Dialog("dialog");
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

        form.add(new AjaxButton("search") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                externalHeatmeterService.callComMeterCursor(model.getObject());

                target.add(container);
            }
        });

        dataTable = new FilteredDataTable<ComMeter>("dataTable", ComMeter.class, "radio", "mId", "mNum", "mDate", "mType") {
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
                onSelect(dataTable.getRadioGroupModel().getObject());

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

    public void open(AjaxRequestTarget target, ComMeterCursor comMeterCursor){
        model.setObject(comMeterCursor);

        target.add(container);
    }

    protected void onSelect(ComMeter comMeter){
    }
}
