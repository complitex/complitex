package ru.complitex.sync.entity;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class SyncParameter {
    private Integer code;

    private List<Sync> syncs;

    private LocalDate date;

    private String locale;

    public SyncParameter() {
    }

    public SyncParameter(LocalDate date, String locale) {
        this.date = date;
        this.locale = locale;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<Sync> getSyncs() {
        return syncs;
    }

    public void setSyncs(List<Sync> syncs) {
        this.syncs = syncs;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "SyncParameter{" +
                (code != null ? "code=" + code : "") +
                (syncs != null ? ", syncs=" + syncs : "") +
                (date != null ? ", date=" + date : "") +
                (locale != null ? ", locale='" + locale + '\'' : "") +
                '}';
    }
}
