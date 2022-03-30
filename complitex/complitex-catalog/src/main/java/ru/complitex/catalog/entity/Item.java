package ru.complitex.catalog.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Ivanov Anatoliy
 */
public class Item extends Entity {
    private Catalog catalog;

    private List<Data> data = new ArrayList<>();

    public Item() {}

    public Item(Catalog catalog) {
        this.catalog = catalog;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Data getData(Long id) {
        Data data = null;

        for (Data d : this.data) {
            if (Objects.equals(d.getId(), id)) {
                if (data != null) {
                    throw new RuntimeException();
                }

                data = d;
            }
        }

        return data;
    }

    public Data getData(Value value) {
        Data data = null;

        for (Data d : this.data) {
            if (Objects.equals(d.getValue().getId(), value.getId())) {
                if (data != null) {
                    throw new RuntimeException();
                }

                data = d;
            }
        }

        return data;
    }

    public Data getOrCreateData(Value value) {
        Data data = getData(value);

        if (data == null) {
            this.data.add(data = new Data(value));
        }

        return data;
    }

    public BigDecimal getNumeric(Value value) {
        Data data = getData(value);

        return data != null ? data.getNumeric() : null;
    }

    public Long getLong(int value) {
        Data data = getData(catalog.getValue(value));

        return data != null ? data.getNumeric().longValue() : null;
    }

    public void setLong(int value, Long numeric) {
        Value val = catalog.getValue(value);

        if (val.getType().getKeyId() == Type.NUMERIC) {
            getOrCreateData(val).setNumeric(numeric != null ? BigDecimal.valueOf(numeric) : null);
        } else {
            throw new IllegalStateException(val.toString());
        }
    }

    public String getText(Value value) {
        Data data = getData(value);

        return data != null ? data.getText() : null;
    }

    public void setText(Value value, String text) {
        getOrCreateData(value).setText(text);
    }

    public void setText(int value, int locale, String text) {
        getOrCreateData(catalog.getValue(value, locale)).setText(text);
    }

    public void setText(int value, String text) {
        getOrCreateData(catalog.getValue(value)).setText(text);
    }

    public String getText(int value, int locale) {
        Data data = getData(catalog.getValue(value, locale));

        return data != null ? data.getText() : null;
    }

    public String getText(int value) {
        Data data = getData(catalog.getValue(value));

        return data != null ? data.getText() : null;
    }

    public LocalDateTime getTimestamp(Value value) {
        Data data = getData(value);

        return data != null ? data.getTimestamp() : null;
    }

    public void setTimestamp(int value, LocalDate date) {
        getOrCreateData(catalog.getValue(value)).setTimestamp(date.atStartOfDay());
    }

    public Long getReferenceId(int value) {
        Data data = getData(catalog.getValue(value));

        return data != null ? data.getReferenceId() : null;
    }

    public void setReferenceId(int value, Long referenceId) {
        Value val = catalog.getValue(value);

        if (val.getType().getKeyId() == Type.REFERENCE) {
            getOrCreateData(val).setReferenceId(referenceId);
        } else {
            throw new IllegalStateException(val.toString());
        }
    }
}
