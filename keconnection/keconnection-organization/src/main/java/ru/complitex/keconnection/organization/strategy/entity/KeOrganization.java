package ru.complitex.keconnection.organization.strategy.entity;

import ru.complitex.common.converter.BooleanConverter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.util.AttributeUtil;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;

import java.util.Date;
import java.util.Locale;

import static ru.complitex.common.util.DateUtil.*;

public class KeOrganization extends DomainObject {

    private Date operatingMonthDate;
    private String parentShortName;

    public KeOrganization(DomainObject copy) {
        super(copy);
    }

    public KeOrganization() {
    }

    public Date getOperatingMonthDate() {
        return operatingMonthDate;
    }

    public void setOperatingMonthDate(Date operatingMonthDate) {
        this.operatingMonthDate = operatingMonthDate;
    }

    public String getOperatingMonth(Locale locale) {
        if (operatingMonthDate == null) {
            return null;
        }
        return displayMonth(getMonth(operatingMonthDate) + 1, locale) + " " + getYear(operatingMonthDate);
    }

    public String getParentShortName() {
        return parentShortName;
    }

    public void setParentShortName(String parentShortName) {
        this.parentShortName = parentShortName;
    }

    public Boolean isReadyCloseOperatingMonth() {
        return AttributeUtil.getAttributeValue(this, KeOrganizationStrategy.READY_CLOSE_OPER_MONTH,
                new BooleanConverter());
    }
}
