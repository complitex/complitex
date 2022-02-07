package ru.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.web.component.dateinput.MaskedDateInput;

import java.util.Date;

/**
 *
 * @author Artem
 */
public final class HeatmeterDateItem extends Panel {

    public HeatmeterDateItem(String id, final IModel<Date> model, boolean editable) {
        super(id);

        Label label = new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return DateUtil.format(model.getObject());
            }
        });
        label.setVisible(!editable);
        add(label);

        WebMarkupContainer inputContainer = new WebMarkupContainer("inputContainer");
        inputContainer.setVisible(editable);
        add(inputContainer);
        MaskedDateInput input = new MaskedDateInput("input", model);
        input.add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        inputContainer.add(input);
    }
}
