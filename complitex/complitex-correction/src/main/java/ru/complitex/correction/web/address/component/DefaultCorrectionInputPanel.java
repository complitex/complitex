package ru.complitex.correction.web.address.component;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Artem
 */
public class DefaultCorrectionInputPanel extends Panel {

    public DefaultCorrectionInputPanel(String id, IModel<String> correctionModel) {
        super(id);
        add(new TextField<String>("correction", correctionModel));
    }
}
