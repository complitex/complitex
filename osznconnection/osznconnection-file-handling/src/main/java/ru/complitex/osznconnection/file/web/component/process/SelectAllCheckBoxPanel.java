package ru.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import ru.complitex.common.web.component.css.CssAttributeBehavior;

/**
 *
 * @author Artem
 */
public final class SelectAllCheckBoxPanel extends Panel {

    public SelectAllCheckBoxPanel(String id) {
        super(id);

        CheckBox selectAll = new CheckBox("selectAll", new Model<>(false)) {

            @Override
            public boolean isEnabled() {
                return true;
                //return !processingManager.isGlobalProcessing(); todo update
            }

            @Override
            public void updateModel() {
                //skip update model
            }
        };
        selectAll.add(new CssAttributeBehavior("processable-list-panel-select-all"));
        add(selectAll);
    }
}
