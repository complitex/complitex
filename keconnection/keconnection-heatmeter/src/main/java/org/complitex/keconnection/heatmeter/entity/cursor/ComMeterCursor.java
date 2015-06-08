package org.complitex.keconnection.heatmeter.entity.cursor;

import org.complitex.common.entity.Cursor;

import java.util.Date;

/**
 * @author inheaven on 08.06.2015 19:58.
 */
public class ComMeterCursor extends Cursor<ComMeter> {
    private String organizationCode;
    private Integer buildingCode;
    private Date om;
    private String serviceCode;

    public ComMeterCursor() {
    }

    public ComMeterCursor(String dataSource, String organizationCode, Integer buildingCode, Date om, String serviceCode) {
        setDataSource(dataSource);

        this.organizationCode = organizationCode;
        this.buildingCode = buildingCode;
        this.om = om;
        this.serviceCode = serviceCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public Integer getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(Integer buildingCode) {
        this.buildingCode = buildingCode;
    }

    public Date getOm() {
        return om;
    }

    public void setOm(Date om) {
        this.om = om;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}
