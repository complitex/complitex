package org.complitex.osznconnection.file.web.component;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.service.StatusRenderService;

import java.util.List;

/**
 *
 * @author Artem
 */
public class StatusRenderer implements IChoiceRenderer<RequestStatus> {

    @Override
    public Object getDisplayValue(RequestStatus object) {
        StatusRenderService statusRenderService = EjbBeanLocator.getBean(StatusRenderService.class);
        return statusRenderService.displayStatus(object, Session.get().getLocale());
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
