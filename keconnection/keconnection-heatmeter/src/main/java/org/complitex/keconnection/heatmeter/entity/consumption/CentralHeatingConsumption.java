package org.complitex.keconnection.heatmeter.entity.consumption;

import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.AbstractEntity;

/**
 * @author inheaven on 016 16.03.15 19:02
 */
public class CentralHeatingConsumption extends AbstractEntity {
    private Long consumptionFileId;

    private String number;
    private String districtCode;
    private String organizationCode;
    private String buildingCode;
    private String accountNumber;
    private String street;
    private String buildingNumber;
    private String commonVolume;
    private String apartmentRange;
    private String beginDate;
    private String endDate;
    private String commonArea;
    private String meterVolume;
    private String meterArea;
    private String commonRentArea;
    private String meterRentVolume;
    private String meterRentArea;
    private String noMeterArea;
    private String noMeterRate;
    private String rate;
    private String noMeterVolume;

    private ConsumptionStatus status;
    private String message;

    public CentralHeatingConsumption() {
    }

    public CentralHeatingConsumption(Long consumptionFileId) {
        this.consumptionFileId = consumptionFileId;
    }

    public Long getConsumptionFileId() {
        return consumptionFileId;
    }

    public void setConsumptionFileId(Long consumptionFileId) {
        this.consumptionFileId = consumptionFileId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getCommonVolume() {
        return commonVolume;
    }

    public void setCommonVolume(String commonVolume) {
        this.commonVolume = commonVolume;
    }

    public String getApartmentRange() {
        return apartmentRange;
    }

    public void setApartmentRange(String apartmentRange) {
        this.apartmentRange = apartmentRange;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCommonArea() {
        return commonArea;
    }

    public void setCommonArea(String commonArea) {
        this.commonArea = commonArea;
    }

    public String getMeterVolume() {
        return meterVolume;
    }

    public void setMeterVolume(String meterVolume) {
        this.meterVolume = meterVolume;
    }

    public String getMeterArea() {
        return meterArea;
    }

    public void setMeterArea(String meterArea) {
        this.meterArea = meterArea;
    }

    public String getCommonRentArea() {
        return commonRentArea;
    }

    public void setCommonRentArea(String commonRentArea) {
        this.commonRentArea = commonRentArea;
    }

    public String getMeterRentVolume() {
        return meterRentVolume;
    }

    public void setMeterRentVolume(String meterRentVolume) {
        this.meterRentVolume = meterRentVolume;
    }

    public String getMeterRentArea() {
        return meterRentArea;
    }

    public void setMeterRentArea(String meterRentArea) {
        this.meterRentArea = meterRentArea;
    }

    public String getNoMeterArea() {
        return noMeterArea;
    }

    public void setNoMeterArea(String noMeterArea) {
        this.noMeterArea = noMeterArea;
    }

    public String getNoMeterRate() {
        return noMeterRate;
    }

    public void setNoMeterRate(String noMeterRate) {
        this.noMeterRate = noMeterRate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getNoMeterVolume() {
        return noMeterVolume;
    }

    public void setNoMeterVolume(String noMeterVolume) {
        this.noMeterVolume = noMeterVolume;
    }

    public ConsumptionStatus getStatus() {
        return status;
    }

    public void setStatus(ConsumptionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEmpty(){
        return Strings.isEmpty(number)
                && Strings.isEmpty(districtCode)
                && Strings.isEmpty(organizationCode)
                && Strings.isEmpty(buildingCode)
                && Strings.isEmpty(accountNumber)
                && Strings.isEmpty(street)
                && Strings.isEmpty(buildingNumber)
                && Strings.isEmpty(commonVolume)
                && Strings.isEmpty(apartmentRange)
                && Strings.isEmpty(beginDate)
                && Strings.isEmpty(endDate)
                && Strings.isEmpty(commonArea)
                && Strings.isEmpty(meterVolume)
                && Strings.isEmpty(meterArea)
                && Strings.isEmpty(commonRentArea)
                && Strings.isEmpty(meterRentVolume)
                && Strings.isEmpty(meterRentArea)
                && Strings.isEmpty(noMeterArea)
                && Strings.isEmpty(noMeterRate)
                && Strings.isEmpty(rate)
                && Strings.isEmpty(noMeterVolume);
    }
}
