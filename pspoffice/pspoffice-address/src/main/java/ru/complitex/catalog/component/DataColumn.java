package ru.complitex.catalog.component;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.catalog.entity.Data;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Type;
import ru.complitex.catalog.entity.Value;
import ru.complitex.ui.component.form.TextInput;
import ru.complitex.ui.component.table.Column;
import ru.complitex.ui.component.table.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ivanov Anatoliy
 */
public class DataColumn extends Column<Item> {
    private final Value value;

    public DataColumn(Value value) {
        this.value = value;

        setHeaderModel(new ResourceModel(value.getResourceKey()));
    }

    @Override
    protected IModel<? extends Serializable> getModel(IModel<Item> model) {
        Item item = model.getObject();

        switch (value.getType().getKeyId()) {
            case Type.NUMERIC -> {
                BigDecimal numeric = item.getNumeric(value);

                return Model.of(numeric != null ? numeric.toPlainString() : "");
            }
            case Type.TIMESTAMP -> {
                LocalDateTime localDateTime = item.getTimestamp(value);

                return () -> localDateTime != null ? localDateTime.toLocalDate() : null;
            }
            default -> {
                return Model.of(model.getObject().getText(value));
            }
        }
    }

    @Override
    public Component newFilter(String id, Table<Item> table) {
        Data data = table.getFilter().getObject().getOrCreateData(value);

        return new TextInput<String>(id, new PropertyModel<>(data, "text"))
                .onChange(table::update);
    }
}
