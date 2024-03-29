package ru.complitex.keconnection.heatmeter.entity;

import ru.complitex.common.entity.ILongId;

import java.util.Date;
import java.util.Objects;

import static ru.complitex.common.util.DateUtil.newDate;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public class HeatmeterPeriod implements ILongId{
    public final static Date DEFAULT_BEGIN_DATE = newDate(1, 10, 2012);
    public final static Date DEFAULT_END_DATE = newDate(31, 12, 2054);

    public final static Date DEFAULT_BEGIN_OM = newDate(1, 10, 2012);
    public final static Date DEFAULT_END_OM = newDate(1, 12, 2054);

    private Long id;
    private Long heatmeterId;
    private Long objectId;
    private HeatmeterPeriodType type;
    private HeatmeterPeriodSubType subType;
    private Date beginDate = DEFAULT_BEGIN_DATE;
    private Date endDate = DEFAULT_END_DATE;
    private Date beginOm = DEFAULT_BEGIN_OM;
    private Date endOm = DEFAULT_END_OM;

    private Date updated;

    public HeatmeterPeriod() {
    }

    public HeatmeterPeriod(HeatmeterPeriodType type) {
        this.type = type;
    }

    public boolean isConnected(HeatmeterPeriod p){
        return beginDate.compareTo(p.endDate) <= 0 && p.beginDate.compareTo(endDate) <= 0;
    }

    public boolean isEncloses(HeatmeterPeriod p){
        return beginDate.compareTo(p.beginDate) <= 0 && endDate.compareTo(p.endDate) >= 0;
    }

    public boolean isSamePeriod(HeatmeterPeriod p){
        return beginDate.equals(p.beginDate) && endDate.equals(p.endDate);
    }

    public boolean isSameValue(HeatmeterPeriod p){
        return Objects.equals(heatmeterId, p.heatmeterId) && Objects.equals(objectId, p.objectId)
                && Objects.equals(type, p.type) && Objects.equals(subType, p.subType);
    }

    public boolean isSame(HeatmeterPeriod p){
        return isSamePeriod(p) && isSameValue(p);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeatmeterId() {
        return heatmeterId;
    }

    public void setHeatmeterId(Long heatmeterId) {
        this.heatmeterId = heatmeterId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public HeatmeterPeriodType getType() {
        return type;
    }

    public void setType(HeatmeterPeriodType type) {
        this.type = type;
    }

    public HeatmeterPeriodSubType getSubType() {
        return subType;
    }

    public void setSubType(HeatmeterPeriodSubType subType) {
        this.subType = subType;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getBeginOm() {
        return beginOm;
    }

    public void setBeginOm(Date beginOm) {
        this.beginOm = beginOm;
    }

    public Date getEndOm() {
        return endOm;
    }

    public void setEndOm(Date endOm) {
        this.endOm = endOm;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
