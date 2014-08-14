package org.complitex.pspoffice.report.html.entity;

import org.complitex.dictionary.entity.ILongId;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 15:19
 */
public class ReportSql implements ILongId, Serializable {
    private Long id;
    private Long reportId;
    private String sql;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


}
