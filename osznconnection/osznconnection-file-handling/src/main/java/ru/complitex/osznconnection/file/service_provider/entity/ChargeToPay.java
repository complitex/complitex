package ru.complitex.osznconnection.file.service_provider.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author Anatoly A. Ivanov
 * 28.03.2019 20:12
 */
public class ChargeToPay {
    private Integer resultCode;

    private String pAcc;
    private Date pDate;
    private BigDecimal pCharge;
    private BigDecimal pToPay;

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getpAcc() {
        return pAcc;
    }

    public void setpAcc(String pAcc) {
        this.pAcc = pAcc;
    }

    public Date getpDate() {
        return pDate;
    }

    public void setpDate(Date pDate) {
        this.pDate = pDate;
    }

    public BigDecimal getpCharge() {
        return pCharge;
    }

    public void setpCharge(BigDecimal pCharge) {
        this.pCharge = pCharge;
    }

    public BigDecimal getpToPay() {
        return pToPay;
    }

    public void setpToPay(BigDecimal pToPay) {
        this.pToPay = pToPay;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChargeToPay.class.getSimpleName() + "[", "]")
                .add("resultCode=" + resultCode)
                .add("pAcc='" + pAcc + "'")
                .add("pDate=" + pDate)
                .add("pCharge=" + pCharge)
                .add("pToPay=" + pToPay)
                .toString();
    }
}
