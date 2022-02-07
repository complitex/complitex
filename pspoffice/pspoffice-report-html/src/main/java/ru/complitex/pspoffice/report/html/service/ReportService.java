package ru.complitex.pspoffice.report.html.service;

import ru.complitex.common.util.StringUtil;
import ru.complitex.pspoffice.report.html.entity.Report;
import ru.complitex.pspoffice.report.html.entity.ReportSql;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.06.12 17:54
 */
@Stateless
public class ReportService {
    @EJB
    private ReportBean reportBean;

    public String fillMarkup(Report report){
        String html = report.getMarkup();

        for (ReportSql reportSql : report.getReportSqlList()){
            List<Map<String, String>> list = reportBean.getSqlList(reportSql);

            if (list == null || list.isEmpty()){
                continue;
            }

            if (list.size() == 1){
                Map<String, String> map = list.get(0);

                for (String key : map.keySet()){
                    html = html.replace(param(key), map.get(key));
                }
            }else {
                html = fillList(list, html);
            }
        }

        return html;
    }

    private String fillList(List<Map<String, String>> list, String html){
        Document document = Jsoup.parse(html);

        Elements elements = document.getElementsByClass("list");

        for (Element element : elements){
            String listInnerMarkup = element.html();

            boolean found = false;
            for (String key : list.get(0).keySet()){
                if (listInnerMarkup.contains(param(key))){
                    found = true;
                    break;
                }
            }

            if (found){
                element.empty();

                for (Map<String, String> map : list){
                    String row = listInnerMarkup;

                    for (String key : map.keySet()){
                        row = row.replace(param(key), StringUtil.emptyOnNull(map.get(key)));
                    }
                    element.append(row);
                }
            }
        }

        return document.outerHtml();
    }

    private String param(String key){
        return "${" + key + "}";
    }
}
