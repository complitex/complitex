package org.complitex.osznconnection.file.entity;

import com.google.common.base.MoreObjects;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.10.2010 17:11:01
 */
public class BenefitData implements Serializable {
    private String firstName;
    private String lastName;
    private String middleName;
    private String inn;
    private String passportSerial;
    private String passportNumber;
    private String orderFamily;
    private String code;
    private String userCount;
    private String budget;
    private Date dateIn;
    private Date dateOut;

    private Long billingId;

    private String privilegeCode;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getPassportSerial() {
        return passportSerial;
    }

    public void setPassportSerial(String passportSerial) {
        this.passportSerial = passportSerial;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getOrderFamily() {
        return orderFamily;
    }

    public void setOrderFamily(String orderFamily) {
        this.orderFamily = orderFamily;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public String getPrivilegeCode() {
        return privilegeCode;
    }

    public void setPrivilegeCode(String osznBenefitCode) {
        this.privilegeCode = osznBenefitCode;
    }

    public Long getBillingId() {
        return billingId;
    }

    public void setBillingId(Long billingId) {
        this.billingId = billingId;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public Date getDateIn() {
        return dateIn;
    }

    public void setDateIn(Date dateIn) {
        this.dateIn = dateIn;
    }

    public Date getDateOut() {
        return dateOut;
    }

    public void setDateOut(Date dateOut) {
        this.dateOut = dateOut;
    }

    public boolean isEmpty() {
        return Strings.isEmpty(inn) && Strings.isEmpty(firstName) && Strings.isEmpty(middleName)
                && Strings.isEmpty(lastName) && Strings.isEmpty(passportSerial) && Strings.isEmpty(passportNumber);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("middleName", middleName)
                .add("inn", inn)
                .add("passportSerial", passportSerial)
                .add("passportNumber", passportNumber)
                .add("orderFamily", orderFamily)
                .add("code", code)
                .add("userCount", userCount)
                .add("budget", budget)
                .add("dateIn", dateIn)
                .add("dateOut", dateOut)
                .add("billingId", billingId)
                .add("privilegeCode", privilegeCode)
                .toString();
    }
}
