package ru.complitex.common.web.component.type;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.Locales;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
public final class StringValuePanel extends Panel {
    @EJB
    private StringLocaleBean stringLocaleBean;

    /**
     * For use in non-ajax environment
     * @param id
     * @param model
     * @param required
     * @param labelModel
     * @param enabled
     */
    public StringValuePanel(String id, IModel<List<StringValue>> model, final boolean required, final IModel<String> labelModel,
                            final boolean enabled) {
        super(id);
        init(model, required, labelModel, enabled, null);
    }

    /**
     * For use in ajax environment
     * @param id
     * @param model
     * @param required
     * @param labelModel
     * @param enabled
     */
    public StringValuePanel(String id, IModel<List<StringValue>> model, final boolean required, final IModel<String> labelModel,
                            final boolean enabled, MarkupContainer[] toUpdate) {
        super(id);
        init(model, required, labelModel, enabled, toUpdate);
    }

    private void init(IModel<List<StringValue>> model, final boolean required, final IModel<String> labelModel, final boolean enabled,
                      final MarkupContainer[] toUpdate) {
        model.getObject().sort(Locales.comparing(StringValue::getLocaleId, Locales.getSystemLocaleId()));

        add(new ListView<StringValue>("strings", model) {

            @Override
            protected void populateItem(ListItem<StringValue> item) {
                StringValue string = item.getModelObject();

                Label language = new Label("language", new ResourceModel(Locales.getLocale(string.getLocaleId()).getLanguage()));
                item.add(language);

                boolean isSystemLocale = false;
                if (stringLocaleBean.getLocaleObject(string.getLocaleId()).isSystem()) {
                    isSystemLocale = true;
                }

                InputPanel<String> inputPanel = new InputPanel<String>("inputPanel", new PropertyModel<String>(string, "value"),
                        String.class, required && isSystemLocale, labelModel, enabled, toUpdate);
                item.add(inputPanel);

                WebMarkupContainer requiredContainer = new WebMarkupContainer("bookFieldRequired");
                requiredContainer.setVisible(isSystemLocale);
                item.add(requiredContainer);
            }
        });
    }
}
