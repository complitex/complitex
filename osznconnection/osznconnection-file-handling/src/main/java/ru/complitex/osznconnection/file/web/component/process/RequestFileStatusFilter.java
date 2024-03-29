package ru.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;

import java.util.Arrays;
import java.util.List;

public class RequestFileStatusFilter extends DropDownChoice<RequestFileStatus> {

    public RequestFileStatusFilter(String id) {
        super(id);

        setChoices(Arrays.asList(RequestFileStatus.values()));
        setChoiceRenderer(new IChoiceRenderer<RequestFileStatus>() {

            @Override
            public Object getDisplayValue(RequestFileStatus object) {
                return RequestFileStatusRenderer.render(object, getLocale());
            }

            @Override
            public String getIdValue(RequestFileStatus object, int index) {
                return object.name();
            }

            @Override
            public RequestFileStatus getObject(String id, IModel<? extends List<? extends RequestFileStatus>> choices) {
                return choices.getObject().stream().filter(c -> id.equals(c.name())).findAny().orElse(null);
            }
        });
        setNullValid(true);
    }
}
