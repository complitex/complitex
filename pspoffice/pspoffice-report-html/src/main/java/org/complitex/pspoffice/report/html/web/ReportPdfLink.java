package org.complitex.pspoffice.report.html.web;

import com.lowagie.text.pdf.BaseFont;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.dictionary.web.component.NoCacheLink;
import org.complitex.pspoffice.report.html.entity.Report;
import org.complitex.pspoffice.report.html.service.ReportBean;
import org.complitex.pspoffice.report.html.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.ejb.EJB;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.06.12 17:54
 */
public class ReportPdfLink extends NoCacheLink {
    private final Logger log = LoggerFactory.getLogger(ReportPdfLink.class);

    private final static String FONT = "org/complitex/flexbuh/report/font/LiberationSans-Regular.ttf";

    @EJB
    private ReportService reportService;

    @EJB
    private ReportBean reportBean;

    private Long reportId;

    public ReportPdfLink(String id, Long reportId) {
        super(id);

        this.reportId = reportId;
    }

    @Override
    public void onClick() {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            final Report report = reportBean.getReport(reportId);

            String html = reportService.fillMarkup(report);

            html = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
                    + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                    + "<html>"
                    + "<head>"
                    + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>"
                    +"<style type=\"text/css\">"
                    + "body {\n" +
                    "    font-family: Liberation Sans;\n" +
//                    "    font-size: 6.8pt;\n" +
//                    "    line-height: 1.17;\n" +
                    "}"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + html
                    + "</body></html>";

            ITextRenderer renderer = new ITextRenderer();

            //Cyrillic font
            URL url = getClass().getClassLoader().getResource(FONT);
            if (url != null) {
                renderer.getFontResolver().addFont(url.getFile(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }

            renderer.setDocumentFromString(html);

            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();

            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                    new AbstractResourceStreamWriter(){

                        @Override
                        public Bytes length() {
                            return Bytes.bytes(outputStream.size());
                        }

                        @Override
                        public String getContentType() {
                            return "application/pdf";
                        }

                        @Override
                        public Time lastModifiedTime() {
                            return Time.now();
                        }

                        @Override
                        public void write(OutputStream output) throws IOException {
                            output.write(outputStream.toByteArray());
                        }
                    }, report.getName() + ".pdf"));
        } catch (Exception e) {
            log.error("Ошибка создания Отчета в формате PDF");
        }
    }
}
