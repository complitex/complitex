package ru.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.web.component.datatable.FilteredDataTable;
import ru.complitex.common.web.component.datatable.column.RadioColumn;
import ru.complitex.common.web.component.dateinput.MaskedDateInput;
import ru.complitex.common.web.component.wiquery.ExtendedDialog;
import ru.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import ru.complitex.keconnection.heatmeter.entity.cursor.ComMeter;
import ru.complitex.keconnection.heatmeter.entity.cursor.ComMeterCursor;
import ru.complitex.keconnection.heatmeter.service.ExternalHeatmeterService;

import javax.ejb.EJB;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author inheaven on 05.06.2015 0:26.
 */
public class ComMeterDialog extends Panel {
    @EJB
    private ExternalHeatmeterService externalHeatmeterService;

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

        FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
        container.add(feedbackPanel);

        Form form = new Form<>("form", model);
        container.add(form);

        form.add(new TextField("organizationCode"));
        form.add(new TextField<>("buildingCode"));
        form.add(new MaskedDateInput("om"));
        form.add(new TextField<>("serviceCode"));

        form.add(new AjaxButton("search", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    externalHeatmeterService.callComMeterCursor(model.getObject());
                } catch (Exception e) {
                    error(getString("error_call"));
                }

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
            protected IColumn<ComMeter, String> getColumn(String field, Field f) {
                if (field.equals("radio")){
                    return new RadioColumn<>();
                }

                return super.getColumn(field, f);
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
