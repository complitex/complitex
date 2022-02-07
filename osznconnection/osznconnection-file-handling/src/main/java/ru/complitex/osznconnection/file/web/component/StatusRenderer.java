package ru.complitex.osznconnection.file.web.component;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.service.StatusRenderUtil;

import java.util.List;

public class StatusRenderer implements IChoiceRenderer<RequestStatus> {

    @Override
    public Object getDisplayValue(RequestStatus object) {
        return StatusRenderUtil.displayStatus(object, Session.get().getLocale());
    }

    @Override
    public String getIdValue(RequestStatus object, int index) {
        return object.name();
    }

    @Override
    public RequestStatus getObject(String id, IModel<? extends List<? extends RequestStatus>> choices) {
        return choices.getObject().stream().filter(c -> id.equals(c.name())).findAny().orElse(null);
    }
}
