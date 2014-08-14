/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import java.io.Serializable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 *
 * @author Artem
 */
public class ExplanationDialog extends Panel {

    public static interface ISubmitAction extends Serializable {

        void onSubmit(AjaxRequestTarget target, String explanation);
    }
    private final Dialog dialog;
    private final FeedbackPanel messages;
    private final IModel<String> explanationModel;
    private ISubmitAction submitAction;
    private final IModel<String> labelModel;
    private final Label label;

    public ExplanationDialog(String id) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(600);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        add(dialog);

        labelModel = new Model<String>();
        label = new Label("label", labelModel);
        label.setOutputMarkupId(true);
        dialog.add(label);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        dialog.add(messages);

        Form<Void> form = new Form<Void>("form");
        dialog.add(form);

        explanationModel = new Model<String>();
        TextArea<String> explanation = new TextArea<String>("explanation", explanationModel);
        explanation.setRequired(true);
        form.add(explanation);

        IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    submitAction.onSubmit(target, explanationModel.getObject());
                } finally {
                    close(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        form.add(submit);
    }

    public void open(AjaxRequestTarget target, String labelText, ISubmitAction submitAction) {
        this.submitAction = submitAction;
        dialog.open(target);
        labelModel.setObject(labelText);
        target.add(label);
    }

    protected void close(AjaxRequestTarget target) {
        dialog.close(target);
    }
}
