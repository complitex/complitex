package ru.complitex.ui.component.form;

import de.agilecoders.wicket.jquery.JQuery;
import de.agilecoders.wicket.jquery.function.Function;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Set;

/**
 * @author Ivanov Anatoliy
 */
public class FormGroup extends Panel {
    private final Label info;

    private boolean required;

    public FormGroup(String id, IModel<String> labelModel) {
        super(id);

        setOutputMarkupId(true);

        add(new Label("label", labelModel){
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                if (FormGroup.this.isRequired()){
                    tag.put("required", "required");
                }
            }
        });

        info = new Label("info", new Model<>());
        info.setOutputMarkupId(true);

        add(info);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        streamChildren()
                .filter(c -> c instanceof FormComponent)
                .forEach(c -> {
                   c.add(new ClassAttributeModifier() {
                       @Override
                       protected Set<String> update(Set<String> oldClasses) {
                           if (info.getDefaultModelObject() != null){
                               oldClasses.add("is-invalid");
                           }

                           return oldClasses;
                       }
                   });
                });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        info.setDefaultModelObject(null);

        streamChildren()
                .filter(c -> c instanceof FormComponent)
                .forEach(c -> {
                    FeedbackMessage message = c.getFeedbackMessages().first(FeedbackMessage.ERROR);

                    if (message != null){
                        info.setDefaultModelObject(message.getMessage());
                        message.markRendered();
                    }
                });
    }

    public String getRemoveErrorJs(){
        return JQuery.$(this)
                .closest(".is-invalid")
                .chain(new Function("removeClass", "is-invalid"))
                .build() + "; " +

                JQuery.$(info)
                        .chain(new Function("hide"))
                        .build();

    }

    public boolean isRequired() {
        return required;
    }

    public FormGroup setRequired(boolean required) {
        this.required = required;

        return this;
    }
}
