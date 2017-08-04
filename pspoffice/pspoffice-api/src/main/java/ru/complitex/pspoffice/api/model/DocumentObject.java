package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 *         25.05.2017 17:03
 */
public class DocumentObject implements Serializable{
    private Long id;
    private Long typeId;
    private String series;
    private String number;
    private String organization;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
