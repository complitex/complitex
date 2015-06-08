package org.complitex.keconnection.heatmeter.entity.cursor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author inheaven on 006 30.04.15 15:39
 */
public class ComMeter implements Serializable{
    private Integer mId;
    private Integer mNum;
    private Date mDate;
    private Integer mType;

    public Integer getMId() {
        return mId;
    }

    public void setMId(Integer mId) {
        this.mId = mId;
    }

    public Integer getMNum() {
        return mNum;
    }

    public void setMNum(Integer mNum) {
        this.mNum = mNum;
    }

    public Date getMDate() {
        return mDate;
    }

    public void setMDate(Date mDate) {
        this.mDate = mDate;
    }

    public Integer getMType() {
        return mType;
    }

    public void setMType(Integer mType) {
        this.mType = mType;
    }
}
