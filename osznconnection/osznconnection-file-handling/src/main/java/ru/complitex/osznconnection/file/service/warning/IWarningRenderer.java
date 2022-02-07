package ru.complitex.osznconnection.file.service.warning;

import ru.complitex.osznconnection.file.entity.RequestWarning;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public interface IWarningRenderer {

    String display(List<RequestWarning> requestWarnings, Locale locale);

    String display(RequestWarning requestWarning, Locale locale);
}
