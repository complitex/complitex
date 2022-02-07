package ru.complitex.pspoffice.report.html.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.util.IdListUtil;
import ru.complitex.pspoffice.report.html.entity.Report;
import ru.complitex.pspoffice.report.html.entity.ReportSql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 15:40
 */
@Stateless
public class ReportBean extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(ReportBean.class);

    private static final String NS = ReportBean.class.getName();

    public ReportSql getReportSql(Long id) {
        return (ReportSql) sqlSession().selectOne(NS + ".selectReportSql", id);
    }

    public Report getReport(Long id) {
        return (Report) sqlSession().selectOne(NS + ".selectReport", id);
    }

    public List<Report> getReportList(FilterWrapper<Report> filterWrapper) {
        return sqlSession().selectList(NS + ".selectReportList", filterWrapper);
    }

    public Long getReportListCount(FilterWrapper<Report> filterWrapper) {
        return sqlSession().selectOne(NS + ".selectReportListCount", filterWrapper);
    }

    public void save(final Report report){
        if (report.getId() == null){
            //insert report
            sqlSession().insert(NS + ".insertReport", report);

            //update report_sql
            for (ReportSql reportSql : report.getReportSqlList()){
                if (reportSql.getSql() != null) {
                    reportSql.setReportId(report.getId());
                    sqlSession().insert(NS + ".insertReportSql", reportSql);
                }
            }
        }else{
            //delete report_sql
            for (ReportSql reportSql : report.getReportSqlList()){
                if (reportSql.getSql() == null){
                    sqlSession().delete(NS + ".deleteReportSql", reportSql.getId());
                }
            }

            List<ReportSql> dbList = getReport(report.getId()).getReportSqlList();
            Iterable<ReportSql> toDelete = IdListUtil.getDiff(dbList, report.getReportSqlList());
            for (ReportSql db : toDelete){
                sqlSession().delete(NS + ".deleteReportSql", db.getId());
            }

            //update report_sql
            for (ReportSql reportSql : report.getReportSqlList()){
                if (reportSql.getId() == null){
                    reportSql.setReportId(report.getId());
                    sqlSession().insert(NS + ".insertReportSql", reportSql);
                }else {
                    sqlSession().update(NS + ".updateReportSql", reportSql);
                }
            }

            //update report
            sqlSession().update(NS + ".updateReport", report);
        }
    }

    public List<Map<String, String>> getSqlList(ReportSql reportSql){
        try {
            ArrayList<Map<String, String>> list = new ArrayList<>();

            getSqlSessionManager().startManagedSession();

            ResultSet rs = getSqlSessionManager().getConnection().createStatement().executeQuery(reportSql.getSql());
            ResultSetMetaData md = rs.getMetaData();

            int count = md.getColumnCount();

            while (rs.next()){
                Map<String, String> map = new HashMap<>();
                list.add(map);

                for (int i = 1; i <= count; ++i){
                    map.put(md.getColumnLabel(i), rs.getString(i));
                }
            }

            getSqlSessionManager().close();

            return list;
        } catch (SQLException e) {
            log.error("Ошибка выполнения запроса", e);
        }

        return null;
    }
}
