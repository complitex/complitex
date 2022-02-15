package ru.complitex.catalog.model;

import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Type;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.entity.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class DataModel<T> implements IModel<T> {
    private final IModel<Item> itemModel;
    private final Value value;

    public DataModel(IModel<Item> itemModel, Value value) {
        this.itemModel = itemModel;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        Data data = itemModel.getObject().getData(value);

        if (data != null) {
            return switch (value.getType().getKeyId()) {
                case Type.NUMERIC -> (T) data.getNumeric();
                case Type.TEXT -> (T) data.getText();
                case Type.TIMESTAMP -> data.getTimestamp() != null ? (T) data.getTimestamp().toLocalDate() : null;
                case Type.REFERENCE -> (T) data.getReferenceId();
                default -> throw new IllegalStateException(value.toString());
            };
        }

        return null;
    }

    @Override
    public void setObject(T object) {
        Data data = itemModel.getObject().getOrCreateData(value);

        switch (value.getType().getKeyId()) {
            case Type.NUMERIC -> data.setNumeric((BigDecimal) object);
            case Type.TEXT  -> data.setText(object != null ? ((String) object).toUpperCase() : null);
            case Type.TIMESTAMP -> data.setTimestamp(data.getTimestamp() != null ? ((LocalDate) object).atStartOfDay() : null);
            case Type.REFERENCE -> data.setReferenceId((Long) object);
        }
    }

    @Override
    public String toString() {
        return "DataModel{" +
                "itemModel=" + itemModel +
                ", value=" + value +
                '}';
    }
}
