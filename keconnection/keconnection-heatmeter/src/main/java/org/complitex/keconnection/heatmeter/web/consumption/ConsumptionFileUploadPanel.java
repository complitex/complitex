package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.util.DateUtil;
import org.complitex.common.web.component.MonthDropDownChoice;
import org.complitex.common.web.component.YearDropDownChoice;
import org.complitex.common.web.component.domain.DomainSelectDialog;
import org.complitex.common.web.component.domain.DomainSelectLabel;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.Date;

import static org.complitex.common.util.DateUtil.*;
import static org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy.SERVICE_PROVIDER;

/**
 * @author inheaven on 18.03.2015 2:03.
 */
public class ConsumptionFileUploadPanel extends Panel {
    public ConsumptionFileUploadPanel(String id) {
        super(id);

        Dialog dialog = new Dialog("dialog");
        dialog.setTitle(new ResourceModel("title"));
        add(dialog);

        Form form = new Form("form");
        dialog.add(form);

        MonthDropDownChoice month = new MonthDropDownChoice("month", new Model<>(getMonth(getCurrentDate()) + 1));
        form.add(month);

        YearDropDownChoice year = new YearDropDownChoice("year", new Model<>(getYear(getCurrentDate())));
        form.add(year);

        IModel<Long> serviceProviderIdModel = Model.of(-1L);
        OrganizationIdPicker serviceProvider = new OrganizationIdPicker("serviceProvider", serviceProviderIdModel, SERVICE_PROVIDER);
        form.add(serviceProvider);

        IModel<Long> serviceIdModel = Model.of(-1L);
        DomainSelectLabel service = new DomainSelectLabel("service", "service", serviceIdModel);
        form.add(service);

        FileUploadField fileUploadField = new FileUploadField("fileUploadField");
        form.add(fileUploadField);

        form.add(new AjaxButton("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.close(target);

                onUpload(DateUtil.newDate(year.getModelObject(), month.getModelObject()),
                        serviceProviderIdModel.getObject(), serviceIdModel.getObject(),fileUploadField);
            }
        });
    }

    protected void onUpload(Date om, Long serviceProviderId, Long serviceId, FileUploadField fileUploadField){
    }
}
