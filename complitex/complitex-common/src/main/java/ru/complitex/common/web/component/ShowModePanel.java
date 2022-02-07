package ru.complitex.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.Arrays;

public class ShowModePanel extends Panel {

    public ShowModePanel(String id, final IModel<ShowMode> model) {
        super(id);

        RadioChoice<ShowMode> showModeChoice = new RadioChoice<ShowMode>("showModeChoice", model,
                Arrays.asList(ShowMode.values()), new EnumChoiceRenderer<>(this));
        showModeChoice.setSuffix("");
        showModeChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ShowModePanel.this.onUpdate(target);
            }
        });
        add(showModeChoice);
    }

    protected void onUpdate(AjaxRequestTarget target){

    }
}
