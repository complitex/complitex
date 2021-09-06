package ru.complitex.ui.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivanov Anatoliy
 */
public class Filter<T extends Serializable> implements Serializable {
    private static final String FILTER_EQUAL = "equal";
    private static final String FILTER_LIKE = "like";

    private static final String OPERATOR_AND = "and";
    private static final String OPERATOR_OR = "or";

    private T object;

    private LocalDate date;

    private Long offset;
    private Long limit;

    private Sort sort;

    private String filter = FILTER_EQUAL;
    private String operator = OPERATOR_AND;

    private Map<String, Object> map = new HashMap<>();

    public Filter() {}

    public Filter(T object) {
        this.object = object;
    }

    public Filter(T object, long offset, long limit) {
        this.object = object;
        this.offset = offset;
        this.limit = limit;
    }

    public Filter(T object, long limit) {
        this.object = object;
        this.limit = limit;
    }

    public Filter(long offset, long limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Filter<T> date(LocalDate date) {
        this.date = date;

        return this;
    }

    public boolean isEqual() {
        return filter.equals(FILTER_EQUAL);
    }

    public Filter<T> equal() {
        filter = FILTER_EQUAL;

        return this;
    }

    public boolean isLike() {
        return filter.equals(FILTER_LIKE);
    }

    public Filter<T> like() {
        filter = FILTER_LIKE;

        return this;
    }

    public boolean isAnd() {
        return operator.equals(OPERATOR_AND);
    }

    public Filter<T> and() {
        operator = OPERATOR_AND;

        return this;
    }

    public boolean isOr() {
        return operator.equals(OPERATOR_OR);
    }

    public Filter<T> or() {
        operator = OPERATOR_OR;

        return this;
    }

    public Filter<T> put(String key, Object value){
        map.put(key, value);

        return this;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Sort getSort() {
        return sort;
    }

    public Filter<T> setSort(Sort sort) {
        this.sort = sort;

        return this;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public String getFilter() {
        return filter;
    }

    public Filter<T> setFilter(String filter) {
        this.filter = filter;

        return this;
    }

    public String getOperator() {
        return operator;
    }

    public Filter<T> setOperator(String operator) {
        this.operator = operator;

        return this;
    }
}
