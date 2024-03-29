package ru.complitex.eirc.payment.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * @author Pavel Sknar
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("ddMMyyyy");

    @Override
    public Date unmarshal(String v) throws Exception {
        return StringUtils.isEmpty(v) ? null : DATE_FORMAT.parseDateTime(v).toDate();
    }

    @Override
    public String marshal(Date v) throws Exception {
        return v == null ? null : DATE_FORMAT.print(v.getTime());
    }
}
