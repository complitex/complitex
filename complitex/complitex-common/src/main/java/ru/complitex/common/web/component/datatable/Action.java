package ru.complitex.common.web.component.datatable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import java.io.Serializable;

/**
 * @author Anatoly Ivanov
 *         Date: 022 22.07.14 16:26
 */
public abstract class Action<T> implements Serializable{
    private IModel<String> nameModel;
    private IModel<String> messageModel;
    private boolean confirm;

    public Action(String nameKey) {
        this(new ResourceModel(nameKey), null, false);
    }

    public Action(String nameKey, String messageKey) {
        this(new ResourceModel(nameKey), new ResourceModel(messageKey), true);
    }

    public Action(IModel<String> nameModel, IModel<String> messageModel, boolean confirm) {
        this.nameModel = nameModel;
        this.messageModel = messageModel;
        this.confirm = confirm;
    }

    public IModel<String> getNameModel() {
        return nameModel;
    }

    public IModel<String> getMessageModel() {
        return messageModel;
    }

    public void onAction(AjaxRequestTarget target, IModel<T> model){
    }

    public boolean isVisible(IModel<T> model){
        return true;
    }

    public boolean isConfirm() {
        return confirm;
    }
}
