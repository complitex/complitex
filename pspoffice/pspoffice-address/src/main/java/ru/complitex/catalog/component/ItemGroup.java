package ru.complitex.catalog.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.ui.component.form.FormGroup;
import ru.complitex.ui.entity.Filter;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class ItemGroup extends FormGroup {
    public ItemGroup(String id, IModel<String> label, int catalog, IModel<Long> model, int value, LocalDate date) {
        super(id, label);

        add(new ItemInput("input", catalog, model, value, date) {
            @Override
            public boolean isRequired() {
                return ItemGroup.this.isRequired();
            }

            @Override
            protected void onFilter(Filter<Item> filter) {
                ItemGroup.this.onFilter(filter);
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                ItemGroup.this.onChange(target);
            }

            @Override
            protected String getTextValue(Item item) {
                String text =  ItemGroup.this.getTextValue(item);

                return text != null ? text : super.getTextValue(item);
            }
        });
    }

    protected void onFilter(Filter<Item> filter) {}

    protected void onChange(AjaxRequestTarget target){}

    protected String getTextValue(Item item){
        return null;
    }
}
