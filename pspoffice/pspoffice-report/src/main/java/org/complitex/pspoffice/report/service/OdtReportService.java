package org.complitex.pspoffice.report.service;

import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.form.FormTextElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.TextDocument;
import org.w3c.dom.NodeList;

import javax.ejb.Stateless;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.05.11 14:58
 */
@Stateless
public class OdtReportService extends AbstractReportService {

    @Override
    public void createReport(String templateName, Map<String, String> values, OutputStream out) throws CreateReportException {
        try {
            TextDocument textDocument = TextDocument.loadDocument(getTemplateInputStream(templateName));
            OfficeTextElement contentRoot = textDocument.getContentRoot();

            NodeList formTexts = contentRoot.getElementsByTagName("form:text");

            for (int i = 0; i < formTexts.getLength(); ++i) {
                FormTextElement formTextElement = (FormTextElement) formTexts.item(i);

                String formId = formTextElement.getFormIdAttribute();
                String name = formTextElement.getFormNameAttribute();

                NodeList drawControls = contentRoot.getElementsByTagName("draw:control");

                for (int j = 0; j < drawControls.getLength(); ++j) {
                    DrawControlElement drawControlElement = (DrawControlElement) drawControls.item(j);

                    if (drawControlElement != null && drawControlElement.getDrawControlAttribute().equals(formId)) {
                        String value = values.get(name);

                        OdfElement parent = (OdfElement) drawControlElement.getParentNode();
                        parent.removeChild(drawControlElement);
                        parent.setTextContent(value != null ? value : "");

                        break;
                    }
                }
            }

            textDocument.save(out);
        } catch (Exception e) {
            throw new CreateReportException(e);
        }
    }
}
