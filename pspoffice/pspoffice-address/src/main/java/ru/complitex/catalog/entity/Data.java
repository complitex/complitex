package ru.complitex.catalog.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Ivanov Anatoliy
 */
public class Data extends Relevance {
    private Value value;

    private Integer index;
    private BigDecimal numeric;
    private String text;
    private LocalDateTime timestamp;
    private Long referenceId;

    public Data() {}

    public Data(Value value) {
        this.value = value;
    }

    public boolean isEqual(Data data) {
        return  Objects.equals(data.getIndex(), getIndex()) &&
                Objects.equals(data.getNumeric(), getNumeric()) &&
                Objects.equals(data.getText(), getText()) &&
                Objects.equals(data.getTimestamp(), getTimestamp()) &&
                Objects.equals(data.getReferenceId(), getReferenceId());
    }

    public boolean isEmpty() {
        return numeric == null && text == null && timestamp == null && referenceId == null;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public BigDecimal getNumeric() {
        return numeric;
    }

    public void setNumeric(BigDecimal numeric) {
        this.numeric = numeric;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Type getType() {
        return getValue().getType();
    }
}
