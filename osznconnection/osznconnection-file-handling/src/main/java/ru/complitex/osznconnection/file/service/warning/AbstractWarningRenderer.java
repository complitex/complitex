package ru.complitex.osznconnection.file.service.warning;

import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.osznconnection.file.entity.RequestWarning;
import ru.complitex.osznconnection.file.entity.RequestWarningParameter;
import ru.complitex.osznconnection.file.entity.RequestWarningStatus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public abstract class AbstractWarningRenderer implements IWarningRenderer {

    private static final Comparator<RequestWarningParameter> WARNING_PARAMETER_COMPARATOR = new Comparator<RequestWarningParameter>() {

        @Override
        public int compare(RequestWarningParameter o1, RequestWarningParameter o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    };

    @Override
    public String display(List<RequestWarning> requestWarnings, Locale locale) {
        String warning = "";

        for (RequestWarning requestWarning : requestWarnings) {
            String currentWarning = display(requestWarning, locale);
            String toAdd = null;
            if (Strings.isEmpty(warning)) {
                toAdd = currentWarning;
            } else {
                toAdd = " " + currentWarning;
            }
            warning = warning + toAdd;
        }
        return warning;
    }

    @Override
    public String display(RequestWarning requestWarning, Locale locale) {
        RequestWarningStatus warningStatus = requestWarning.getStatus();
        Object[] messageParams = null;
        List<RequestWarningParameter> parameters = requestWarning.getParameters();
        if (!parameters.isEmpty()) {
            Collections.sort(parameters, WARNING_PARAMETER_COMPARATOR);
            messageParams = new Object[parameters.size()];
            for (int i = 0; i < parameters.size(); i++) {
                Object parameterValue = handleParameterValue(parameters.get(i), locale);
                messageParams[i] = parameterValue;
            }
        }
        return getMessage(warningStatus, messageParams, locale);
    }

    protected String getMessage(RequestWarningStatus warningStatus, Object[] params, Locale locale) {
        return ResourceUtil.getFormatString(getBundle(), warningStatus.name(), locale, params);
    }

    protected abstract String getBundle();

    protected Object handleParameterValue(RequestWarningParameter parameter, Locale locale) {
        String type = parameter.getType();
        String value = parameter.getValue();

        if (Strings.isEmpty(type)) {
            return value;
        }

        return handleReferenceType(type, value, locale);
    }

    protected Object handleDouble(String value, Locale locale) {
        Double doubleValue = Double.valueOf(value);
        return doubleValue;
    }

    protected String handleReferenceType(String type, String value, Locale locale) {
        Long objectId = null;
        try {
            objectId = Long.valueOf(value);
            return displayObject(type, objectId, locale);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    protected String displayObject(String entity, long objectId, Locale locale) {
        IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entity);
        DomainObject object = strategy.getDomainObject(objectId, false);
        if (object != null) {
            return strategy.displayDomainObject(object, locale);
        }
        return null;
    }
}
