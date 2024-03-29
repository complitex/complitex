package ru.complitex.logging.web.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import ru.complitex.common.entity.LogChange;
import ru.complitex.common.util.StringUtil;

import java.util.List;
import org.apache.wicket.util.string.Strings;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.08.2010 17:24:36
 */
public class LogChangePanel extends Panel {

    public LogChangePanel(String id, List<LogChange> logChanges) {
        super(id);

        ListView<LogChange> listView = new ListView<LogChange>("log_changes", logChanges) {

            @Override
            protected void populateItem(ListItem<LogChange> item) {
                LogChange logChange = item.getModelObject();

                item.add(new Label("attribute_id", StringUtil.valueOf(logChange.getAttributeId())));
                item.add(new Label("collection", StringUtil.valueOf(logChange.getCollection())));
                item.add(new Label("property", StringUtil.valueOf(logChange.getProperty())));
                item.add(new Label("old_value", getValueOrNull(logChange.getOldValue())));
                item.add(new Label("new_value", getValueOrNull(logChange.getNewValue())));
                item.add(new Label("locale", StringUtil.valueOf(logChange.getLocale())));
            }
        };

        add(listView);
    }

    private String getValueOrNull(String value) {
        return Strings.isEmpty(value) ? getString("null_value") : value;
    }
}
