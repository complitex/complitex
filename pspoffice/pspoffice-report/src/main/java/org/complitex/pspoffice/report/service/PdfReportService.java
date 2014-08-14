package org.complitex.pspoffice.report.service;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import javax.ejb.Stateless;
import java.io.*;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.05.11 18:07
 */
@Stateless
public class PdfReportService extends AbstractReportService {

    private final static String FONT = "org/complitex/pspoffice/report/font/times.ttf";

    @Override
    public void createReport(String templateName, Map<String, String> values, OutputStream out) throws CreateReportException {
        try {
            PdfStamper stamper = new PdfStamper(new PdfReader(getTemplateInputStream(templateName)), out);
            stamper.setFormFlattening(true);

            AcroFields acroFields = stamper.getAcroFields();

            acroFields.addSubstitutionFont(BaseFont.createFont(getFontURLPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED));

            for (String key : values.keySet()) {
                acroFields.setField(key.toLowerCase(), values.get(key));
            }

            stamper.close();
        } catch (Exception e) {
            throw new CreateReportException(e);
        }
    }

    private String getFontURLPath() {
        return getClass().getClassLoader().getResource(FONT).getFile();
    }
}
