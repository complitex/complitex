package org.complitex.keconnection.heatmeter.entity.cursor;

import java.util.Date;

/**
 * @author inheaven on 006 30.04.15 15:39
 */
public class ComMeter {
    private Integer mId;
    private Integer mNum;
    private Date mDate;
    private Integer mType;

    public Integer getmId() {
        return mId;
    }

    public void setmId(Integer mId) {
        this.mId = mId;
    }

    public Integer getmNum() {
        return mNum;
    }

    public void setmNum(Integer mNum) {
        this.mNum = mNum;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public Integer getmType() {
        return mType;
    }

    public void setmType(Integer mType) {
        this.mType = mType;
    }
}
