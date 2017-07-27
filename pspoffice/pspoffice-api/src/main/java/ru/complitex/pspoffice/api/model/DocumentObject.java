package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 *         25.05.2017 17:03
 */
public class DocumentObject implements Serializable{
    private Long objectId;
    private Long typeId;
    private String series;
    private String number;
    private String organization;
    private Date date;

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
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
