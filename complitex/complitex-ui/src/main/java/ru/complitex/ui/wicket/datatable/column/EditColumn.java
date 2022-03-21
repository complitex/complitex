package ru.complitex.ui.wicket.datatable.column;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.model.Model;
import ru.complitex.ui.wicket.link.LinkPanel;

/**
 * @author Anatoly A. Ivanov
 * 09.08.2017 17:14
 */
public abstract class EditColumn<T> extends FilteredAbstractColumn<T, String> {
    public EditColumn() {
        super(Model.of(""));
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new LinkPanel(componentId, new BootstrapLink<Void>(LinkPanel.LINK_COMPONENT_ID, Buttons.Type.Menu){
            @Override
            public void onClick() {

            }
        }.setSize(Buttons.Size.Small)).add(new AjaxFormSubmitBehavior("click") {});
    }
}
