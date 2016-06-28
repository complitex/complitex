package org.complitex.common.entity;

import com.google.common.base.CaseFormat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.04.12 16:53
 */
public class FilterWrapper<T extends Serializable> implements Serializable{
    private T object;

    private long first = 0;
    private long count = 0;
    private String sortProperty = "id";
    private boolean ascending = false;

    private boolean like = false;
    private boolean regexp = false;
    private boolean required = false;

    private boolean admin = false;
    private String userOrganizationsString;
    private String outerOrganizationsString;

    private Map<String, Object> map = new HashMap<>();

    private StringLocale stringLocale;

    private boolean camelToUnderscore = true;

    private boolean nullable = false;

    public FilterWrapper() {
    }

    public FilterWrapper(T object) {
        this.object = object;
    }

    public FilterWrapper(T object, long first, long count) {
        this.object = object;
        this.first = first;
        this.count = count;
    }

    public static <T extends Serializable> FilterWrapper<T> of(T object){
        return new FilterWrapper<>(object);
    }

    public static <T extends Serializable> FilterWrapper<T> of(T object, long first, long count){
        return new FilterWrapper<>(object, first, count);
    }

    public FilterWrapper<T> add(String key, Object value){
        map.put(key, value);

        return this;
    }

    public String getAsc(){
        return ascending ? "asc" : "desc";
    }

    public String getLimit(){
        return count > 0 ? " limit " + first + ", " + count : "";
    }

    public String getOrderLimit(){
        return "order by `" + getSortProperty() + "` " + getAsc() + getLimit();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public long getFirst() {
        return first > 0 ? first : 0;
    }

    public void setFirst(long first) {
        this.first = first;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getSortProperty() {
        if (sortProperty == null){
            return "id";
        }

        if (camelToUnderscore){
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortProperty);
        }

        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public StringLocale getStringLocale() {
        return stringLocale;
    }

    public void setStringLocale(StringLocale stringLocale) {
        this.stringLocale = stringLocale;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isRegexp() {
        return regexp;
    }

    public void setRegexp(boolean regexp) {
        this.regexp = regexp;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getUserOrganizationsString() {
        return userOrganizationsString;
    }

    public void setUserOrganizationsString(String userOrganizationsString) {
        this.userOrganizationsString = userOrganizationsString;
    }

    public String getOuterOrganizationsString() {
        return outerOrganizationsString;
    }

    public void setOuterOrganizationsString(String outerOrganizationsString) {
        this.outerOrganizationsString = outerOrganizationsString;
    }

    public boolean isCamelToUnderscore() {
        return camelToUnderscore;
    }

    public void setCamelToUnderscore(boolean camelToUnderscore) {
        this.camelToUnderscore = camelToUnderscore;
    }

    public boolean isNullable() {
        return nullable;
    }

    public FilterWrapper<T> setNullable(boolean nullable) {
        this.nullable = nullable;

        return this;
    }
}
