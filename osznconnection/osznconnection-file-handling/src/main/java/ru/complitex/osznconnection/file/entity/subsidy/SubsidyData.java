package ru.complitex.osznconnection.file.entity.subsidy;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 25.01.2018 20:32
 */
public class SubsidyData {
    private BigDecimal sm1, sm2, sm3, sm4, sm5, sm6, sm7, sm8;
    private Date opMonth;
    private Date subsMonth;

    public BigDecimal getSm1() {
        return sm1;
    }

    public void setSm1(BigDecimal sm1) {
        this.sm1 = sm1;
    }

    public BigDecimal getSm2() {
        return sm2;
    }

    public void setSm2(BigDecimal sm2) {
        this.sm2 = sm2;
    }

    public BigDecimal getSm3() {
        return sm3;
    }

    public void setSm3(BigDecimal sm3) {
        this.sm3 = sm3;
    }

    public BigDecimal getSm4() {
        return sm4;
    }

    public void setSm4(BigDecimal sm4) {
        this.sm4 = sm4;
    }

    public BigDecimal getSm5() {
        return sm5;
    }

    public void setSm5(BigDecimal sm5) {
        this.sm5 = sm5;
    }

    public BigDecimal getSm6() {
        return sm6;
    }

    public void setSm6(BigDecimal sm6) {
        this.sm6 = sm6;
    }

    public BigDecimal getSm7() {
        return sm7;
    }

    public void setSm7(BigDecimal sm7) {
        this.sm7 = sm7;
    }

    public BigDecimal getSm8() {
        return sm8;
    }

    public void setSm8(BigDecimal sm8) {
        this.sm8 = sm8;
    }

    public Date getOpMonth() {
        return opMonth;
    }

    public void setOpMonth(Date opMonth) {
        this.opMonth = opMonth;
    }

    public Date getSubsMonth() {
        return subsMonth;
    }

    public void setSubsMonth(Date subsMonth) {
        this.subsMonth = subsMonth;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sm1", sm1)
                .add("sm2", sm2)
                .add("sm3", sm3)
                .add("sm4", sm4)
                .add("sm5", sm5)
                .add("sm6", sm6)
                .add("sm7", sm7)
                .add("sm8", sm8)
                .add("opMonth", opMonth)
                .add("subsMonth", subsMonth)
                .toString();
    }
}
