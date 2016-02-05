package org.complitex.common.web.component.type;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.complitex.common.util.ResourceUtil;

import java.util.Locale;

/**
 *
 * @author Artem
 */
public class BooleanPanel extends Panel {

    private static final String RESOURCE_BUNDLE = BooleanPanel.class.getName();

    public BooleanPanel(String id, IModel<Boolean> model, IModel<String> labelModel, boolean enabled) {
        super(id);
        CheckBox checkBox = new CheckBox("checkbox", model);
        checkBox.setEnabled(enabled);
        checkBox.setLabel(labelModel);
        checkBox.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });

        add(checkBox);
    }

    public static String display(boolean value, Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, resourceKey(value), locale);
    }

    private static String resourceKey(boolean value) {
        return String.valueOf(value);
    }
}
