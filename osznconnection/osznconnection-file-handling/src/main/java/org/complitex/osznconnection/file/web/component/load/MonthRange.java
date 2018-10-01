package org.complitex.osznconnection.file.web.component.load;

import java.io.Serializable;

public class MonthRange implements Serializable {

    private Integer monthFrom;
    private Integer monthTo;

    public MonthRange(Integer monthFrom, Integer monthTo) {
        this.monthFrom = monthFrom;
        this.monthTo = monthTo;
    }

    public MonthRange(Integer month) {
        this(month, month);
    }

    public Integer getMonthFrom() {
        return monthFrom;
    }

    public Integer getMonthTo() {
        return monthTo;
    }
}
