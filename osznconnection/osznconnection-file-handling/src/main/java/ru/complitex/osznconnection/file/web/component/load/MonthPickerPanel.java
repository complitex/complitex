package ru.complitex.osznconnection.file.web.component.load;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.web.component.MonthDropDownChoice;

/**
 *
 * @author Artem
 */
public final class MonthPickerPanel extends Panel {

    public MonthPickerPanel(String id, IModel<Integer> model) {
        super(id);

        MonthDropDownChoice month = new MonthDropDownChoice("month", model);
        month.setRequired(true);
        add(month);
    }
}
