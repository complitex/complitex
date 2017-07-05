package ru.complitex.pspoffice.api.model;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 *         25.05.2017 17:03
 */
public class DocumentObject implements Serializable{
    private Long objectId;
    private Long type;
    private String series;
    private String number;
    private String organizationIssued;
    private String dateIssued;

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
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

    public String getOrganizationIssued() {
        return organizationIssued;
    }

    public void setOrganizationIssued(String organizationIssued) {
        this.organizationIssued = organizationIssued;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }
}
