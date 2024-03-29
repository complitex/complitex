package ru.complitex.common.web.component.type;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.web.component.dateinput.MaskedDateInput;

import java.util.Date;

/**
 *
 * @author Artem
 */
public final class MaskedDateInputPanel extends Panel {
    public static final String DATE_INPUT_ID = "input";

    public MaskedDateInputPanel(String id, IModel<Date> model, boolean required, IModel<String> labelModel, boolean enabled) {
        super(id);

        MaskedDateInput input = new MaskedDateInput(DATE_INPUT_ID, model);
        input.setEnabled(enabled);
        input.setLabel(labelModel);
        input.setRequired(required);
        add(input);
    }

    public MaskedDateInputPanel(String id, IModel<Date> model) {
        this(id, model, false, null, true);
    }
}
