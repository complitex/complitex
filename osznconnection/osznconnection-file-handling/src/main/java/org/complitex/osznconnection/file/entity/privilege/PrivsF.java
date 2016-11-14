package org.complitex.osznconnection.file.entity.privilege;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author inheaven on 010 10.11.16.
 */
public class PrivsF {
    private Integer depart;
    private Integer cdpr;
    private String idCode;
    private String fio;
    private String ppos;
    private String rs;
    private Integer yearId;
    private Integer monthIn;
    private Integer lgCode;
    private Date data1;
    private Date data2;
    private Integer lgKol;
    private String lgKat;
    private Integer lgPrc;
    private BigDecimal summ;
    private BigDecimal fact;
    private BigDecimal tarif;
    private Integer flag;

    public Integer getDepart() {
        return depart;
    }

    public void setDepart(Integer depart) {
        this.depart = depart;
    }

    public Integer getCdpr() {
        return cdpr;
    }

    public void setCdpr(Integer cdpr) {
        this.cdpr = cdpr;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getPpos() {
        return ppos;
    }

    public void setPpos(String ppos) {
        this.ppos = ppos;
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public Integer getYearId() {
        return yearId;
    }

    public void setYearId(Integer yearId) {
        this.yearId = yearId;
    }

    public Integer getMonthIn() {
        return monthIn;
    }

    public void setMonthIn(Integer monthIn) {
        this.monthIn = monthIn;
    }

    public Integer getLgCode() {
        return lgCode;
    }

    public void setLgCode(Integer lgCode) {
        this.lgCode = lgCode;
    }

    public Date getData1() {
        return data1;
    }

    public void setData1(Date data1) {
        this.data1 = data1;
    }

    public Date getData2() {
        return data2;
    }

    public void setData2(Date data2) {
        this.data2 = data2;
    }

    public Integer getLgKol() {
        return lgKol;
    }

    public void setLgKol(Integer lgKol) {
        this.lgKol = lgKol;
    }

    public String getLgKat() {
        return lgKat;
    }

    public void setLgKat(String lgKat) {
        this.lgKat = lgKat;
    }

    public Integer getLgPrc() {
        return lgPrc;
    }

    public void setLgPrc(Integer lgPrc) {
        this.lgPrc = lgPrc;
    }

    public BigDecimal getSumm() {
        return summ;
    }

    public void setSumm(BigDecimal summ) {
        this.summ = summ;
    }

    public BigDecimal getFact() {
        return fact;
    }

    public void setFact(BigDecimal fact) {
        this.fact = fact;
    }

    public BigDecimal getTarif() {
        return tarif;
    }

    public void setTarif(BigDecimal tarif) {
        this.tarif = tarif;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
