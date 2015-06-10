package org.complitex.common.web.component.form;

import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author inheaven on 10.06.2015 18:38.
 */
public class RadioPanel<T> extends Panel {
    public RadioPanel(String id, IModel<T> model) {
        super(id);

        add(new Radio<T>("radio", model));
    }
}
