package ru.complitex.common.web.component.datatable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author inheaven on 031 31.03.15 18:08
 */
public class FilteredActionInput<T> extends Panel{
    private ActionDialogPanel<T> actionDialogPanel;

    public FilteredActionInput(String id) {
        super(id);

        add(new AjaxSubmitLink("filter") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                FilteredActionInput.this.onSubmit(target);
            }
        });

        add(actionDialogPanel = new ActionDialogPanel<>("action_dialog"));
    }

    public ActionDialogPanel<T> getActionDialogPanel() {
        return actionDialogPanel;
    }

    protected void onSubmit(AjaxRequestTarget target){

    }
}
