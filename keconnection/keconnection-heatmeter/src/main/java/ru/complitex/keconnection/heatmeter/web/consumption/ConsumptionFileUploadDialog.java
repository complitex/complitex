package ru.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.web.component.MonthDropDownChoice;
import ru.complitex.common.web.component.YearDropDownChoice;
import ru.complitex.common.web.component.domain.DomainSelectLabel;
import ru.complitex.common.web.component.organization.OrganizationIdPicker;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.Date;

import static ru.complitex.common.util.DateUtil.*;
import static ru.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy.SERVICE_PROVIDER_TYPE;
import static ru.complitex.organization_type.strategy.OrganizationTypeStrategy.USER_ORGANIZATION_TYPE;

/**
 * @author inheaven on 18.03.2015 2:03.
 */
public abstract class ConsumptionFileUploadDialog extends Panel {
    private Dialog dialog;

    public ConsumptionFileUploadDialog(String id) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setTitle(new ResourceModel("title"));
        dialog.setWidth(450);
        dialog.setModal(true);
        add(dialog);

        Form form = new Form("form");
        dialog.add(form);

        MonthDropDownChoice month = new MonthDropDownChoice("month", new Model<>(getMonth(getCurrentDate()) + 1));
        form.add(month);

        YearDropDownChoice year = new YearDropDownChoice("year", new Model<>(getYear(getCurrentDate())));
        form.add(year);

        IModel<Long> serviceProviderIdModel = Model.of(-1L);
        OrganizationIdPicker serviceProvider = new OrganizationIdPicker("serviceProvider", serviceProviderIdModel, SERVICE_PROVIDER_TYPE);
        form.add(serviceProvider);

        IModel<Long> serviceIdModel = Model.of(-1L);
        DomainSelectLabel service = new DomainSelectLabel("service", "service", serviceIdModel);
        form.add(service);

        IModel<Long> userOrganizationIdModel = Model.of(-1L);
        OrganizationIdPicker userOrganization = new OrganizationIdPicker("userOrganization", userOrganizationIdModel, USER_ORGANIZATION_TYPE);
        form.add(userOrganization);

        FileUploadField fileUploadField = new FileUploadField("fileUploadField");
        form.add(fileUploadField);

        form.add(new AjaxButton("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.close(target);

                onUpload(target, DateUtil.newDate(year.getModelObject(), month.getModelObject()),
                        serviceProviderIdModel.getObject(), serviceIdModel.getObject(),
                        userOrganizationIdModel.getObject(), fileUploadField);
            }
        });
    }

    protected abstract void onUpload(AjaxRequestTarget target, Date om, Long serviceProviderId, Long serviceId,
                                     Long userOrganizationId, FileUploadField fileUploadField);

    public void open(AjaxRequestTarget target){
        dialog.open(target);
    }
}
