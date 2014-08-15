package org.complitex.common.web.component.organization;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;

import javax.ejb.EJB;

public class OrganizationPicker extends FormComponentPanel<DomainObject> {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    protected IOrganizationStrategy organizationStrategy;

    private OrganizationPickerDialog pickerDialog;
    private Label organizationLabel;

    public OrganizationPicker(String id, IModel<DomainObject> model, Long... organizationTypeIds) {
        super(id, model);

        pickerDialog = new OrganizationPickerDialog("dialog", model, organizationTypeIds){
            @Override
            protected void onSelect(AjaxRequestTarget target) {
                target.add(organizationLabel);

                OrganizationPicker.this.onSelect(target);
            }
        };
        add(pickerDialog);

        organizationLabel = new Label("organizationLabel",
                new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        DomainObject organization = getModelObject();
                        if (organization != null) {
                            return organizationStrategy.displayShortNameAndCode(organization, getLocale());
                        } else {
                            return getString("organization_not_selected");
                        }
                    }
                });
        organizationLabel.setOutputMarkupId(true);
        add(organizationLabel);

        AjaxLink<Void> choose = new AjaxLink<Void>("choose") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                pickerDialog.open(target);
            }
        };

        add(choose);
    }

    protected void onSelect(AjaxRequestTarget target){
    }
}
