package ru.complitex.pspoffice.report.web;

import ru.complitex.pspoffice.report.entity.IReportField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import ru.complitex.common.util.StringUtil;

public abstract class AbstractReportDownload<T extends Serializable> implements Serializable {

    private final String reportName;
    private final IReportField[] reportFields;
    private final T report;

    protected AbstractReportDownload(final String reportName, final IReportField[] reportFields, T report) {
        this.reportName = reportName;
        this.reportFields = reportFields;
        this.report = report;
    }

    protected final T getReport() {
        return report;
    }

    public final IReportField[] getReportFields() {
        return reportFields;
    }

    public final String getReportName() {
        return reportName;
    }

    protected final Map<IReportField, Object> newValuesMap() {
        return new HashMap<IReportField, Object>();
    }

    protected final void putMultilineValue(Map<IReportField, Object> values, String value, int lineSize, IReportField... fields) {
        if (value == null) {
            return;
        }

        String[] wrap = StringUtil.wrap(value, lineSize, "\n", true).split("\n", fields.length);
        int index = 0;
        for (IReportField field : fields) {
            if (index < wrap.length) {
                values.put(field, wrap[index++]);
            }
        }
    }

    public abstract Map<IReportField, Object> getValues(Locale locale);

    public abstract String getFileName(Locale locale);
}
