package org.complitex.pspoffice.report.html.entity;

import org.complitex.common.entity.ILongId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 15:19
 */
public class Report implements ILongId, Serializable {
    private Long id;
    private String name;
    private String markup;
    private Date updated;

    private List<ReportSql> reportSqlList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarkup() {
        return markup;
    }

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<ReportSql> getReportSqlList() {
        return reportSqlList;
    }

    public void setReportSqlList(List<ReportSql> reportSqlList) {
        this.reportSqlList = reportSqlList;
    }
}
