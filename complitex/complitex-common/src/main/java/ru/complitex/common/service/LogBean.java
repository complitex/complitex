package ru.complitex.common.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.Module;
import ru.complitex.common.entity.*;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.util.Numbers;
import ru.complitex.common.util.StringUtil;

import javax.annotation.Resource;
import javax.ejb.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 17:50:26
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class LogBean extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(LogBean.class);

    public static final String STATEMENT_PREFIX = LogBean.class.getCanonicalName();

    public static final int MAX_DESCRIPTION_LENGTH = 255;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @Resource
    private SessionContext sessionContext;

    @EJB
    private StringValueBean stringBean;
    
    /**
     * Records user log in action. Should be invoked from within user log in code. 
     * Due to glassfish bug: {@link SessionContext#getCallerPrincipal() } always returns null from within web listeners, 
     * logIn() method takes user login as argument.
     * @param userLogin user login
     * @param module
     * @param controllerClass
     * @param descriptionPattern
     * @param descriptionArguments 
     */
    public void logIn(String userLogin, String module, Class controllerClass, String descriptionPattern, 
            Object... descriptionArguments){
        log(userLogin, module, controllerClass != null ? controllerClass.getName() : null, null, null, Log.EVENT.USER_LOGIN,
                Log.STATUS.OK, null, descriptionPattern, descriptionArguments);
    }
    
    /**
     * Records user log out action. Should be invoked from within user log out code. 
     * Due to glassfish bug: {@link SessionContext#getCallerPrincipal() } always returns null from within web listeners, 
     * logOut() method takes user login as argument.
     * @param userLogin user login
     * @param module
     * @param controllerClass
     * @param descriptionPattern
     * @param descriptionArguments 
     */
    public void logOut(String userLogin, String module, Class controllerClass, String descriptionPattern, 
            Object... descriptionArguments){
        log(userLogin, module, controllerClass != null ? controllerClass.getName() : null, null, null, Log.EVENT.USER_LOGOFF, 
                Log.STATUS.OK, null, descriptionPattern, descriptionArguments);
    }

    public void info(String module, Class controllerClass, Class modelClass, Long objectId, Log.EVENT event,
            String descriptionPattern, Object... descriptionArguments) {

        String controller = controllerClass != null ? controllerClass.getName() : null;
        String model = modelClass != null ? modelClass.getName() : null;

        log(module, controller, model, objectId, event, Log.STATUS.OK, null, descriptionPattern, descriptionArguments);
    }

    public void error(String module, Class controllerClass, Class modelClass, Long objectId, Log.EVENT event,
            String descriptionPattern, Object... descriptionArguments) {

        String controller = controllerClass != null ? controllerClass.getName() : null;
        String model = modelClass != null ? modelClass.getName() : null;

        log(module, controller, model, objectId, event, Log.STATUS.ERROR, null, descriptionPattern, descriptionArguments);
    }

    public void warn(String module, Class controllerClass, Class modelClass, Long objectId, Log.EVENT event,
            String descriptionPattern, Object... descriptionArguments) {

        String controller = controllerClass != null ? controllerClass.getName() : null;
        String model = modelClass != null ? modelClass.getName() : null;

        log(module, controller, model, objectId, event, Log.STATUS.WARN, null, descriptionPattern, descriptionArguments);
    }

    public void info(String module, Class controllerClass, Class modelClass, String entityName, Long objectId,
                     Log.EVENT event, List<LogChange> changes, String descriptionPattern, Object... descriptionArguments) {

        String controller = controllerClass != null ? controllerClass.getName() : null;
        String model = modelClass != null ? modelClass.getName() + (entityName != null ? "#" + entityName : "") : null;

        log(module, controller, model, objectId, event, Log.STATUS.OK, changes, descriptionPattern, descriptionArguments);
    }

    public void log(Log.STATUS status, String module, Class controllerClass, Log.EVENT event,
                    IStrategy strategy, DomainObject oldDomainObject, DomainObject newDomainObject,
                    String descriptionPattern, Object... descriptionArguments) {

        String controller = controllerClass != null ? controllerClass.getName() : null;
        String model = DomainObject.class.getName() + "#" + strategy.getEntityName();

        log(module, controller, model, newDomainObject.getObjectId(), event, status,
                getLogChanges(strategy, oldDomainObject, newDomainObject),
                descriptionPattern, descriptionArguments);
    }

    public void logArchivation(Log.STATUS status, String module, Class controllerClass, String entityName, long objectId,
            String descriptionPattern, Object... descriptionArguments){
        String controller = controllerClass != null ? controllerClass.getName() : null;
        String model = DomainObject.class.getName() + "#" + entityName;
        log(module, controller, model, objectId, Log.EVENT.ARCHIVE, status, null, descriptionPattern, descriptionArguments);
    }

    public void logReplacePermissions(Log.STATUS status, String entity, Long objectId, String descriptionPattern, Object... descriptionArguments) {
        String controller = "replacePermissions";
        String model = DomainObject.class.getName() + "#" + entity;
        String module = Module.NAME;
        log(module, controller, model, objectId, Log.EVENT.SETTING_PERMISSION, status, null, descriptionPattern, descriptionArguments);
    }

    public void logChangePermissions(Log.STATUS status, String entity, Long objectId, String descriptionPattern, Object... descriptionArguments) {
        String controller = "changePermissions";
        String model = DomainObject.class.getName() + "#" + entity;
        String module = Module.NAME;
        log(module, controller, model, objectId, Log.EVENT.SETTING_PERMISSION, status, null, descriptionPattern, descriptionArguments);
    }

    public void logChangeActivity(Log.STATUS status, String entity, Long objectId, boolean enable, String descriptionPattern, Object... descriptionArguments){
        String controller = "changeActivity";
        String model = DomainObject.class.getName() + "#" + entity;
        String module = Module.NAME;
        log(module, controller, model, objectId, enable ? Log.EVENT.ENABLE : Log.EVENT.DISABLE, status, null, descriptionPattern, descriptionArguments);
    }

    public void error(String module, Class controllerClass, Class modelClass, String entityName, Long objectId,
            Log.EVENT event, List<LogChange> changes, String descriptionPattern, Object... descriptionArguments) {

        String controller = controllerClass != null ? controllerClass.getName() : null;
        String model = modelClass != null ? modelClass.getName() + (entityName != null ? ":" + entityName : "") : null;

        log(module, controller, model, objectId, event, Log.STATUS.ERROR, changes, descriptionPattern, descriptionArguments);
    }
    
    private void log(String module, String controller, String model, Long objectId, Log.EVENT event, Log.STATUS status, 
            List<LogChange> logChanges, String descriptionPattern, Object... descriptionArguments){
        log(sessionContext.getCallerPrincipal().getName(), module, controller, model, objectId, event, status, 
                logChanges, descriptionPattern, descriptionArguments);
    }

    private void log(String login, String module, String controller, String model, Long objectId, Log.EVENT event, Log.STATUS status, 
            List<LogChange> logChanges, String descriptionPattern, Object... descriptionArguments) {
        Log log = new Log();

        log.setDate(DateUtil.getCurrentDate());
        log.setLogin(login);
        log.setModule(module);
        log.setController(controller);
        log.setModel(model);
        log.setObjectId(objectId);
        log.setEvent(event);
        log.setStatus(status);
        log.setLogChanges(logChanges);

        try {
            log.setDescription(descriptionPattern != null && descriptionArguments != null
                    ? MessageFormat.format(descriptionPattern, descriptionArguments)
                    : descriptionPattern);
        } catch (Exception e) {
            log.setDescription(descriptionPattern + " " + Arrays.toString(descriptionArguments));
        }

        if (log.getDescription() != null && log.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.setDescription(log.getDescription().substring(0, MAX_DESCRIPTION_LENGTH));
        }

        //open new session
        SqlSession session = getSqlSessionManager().openSession();

        session.insert(STATEMENT_PREFIX + ".insertLog", log);

        if (log.getLogChanges() != null && !log.getLogChanges().isEmpty()) {
            for (LogChange logChange : log.getLogChanges()) {
                logChange.setLogId(log.getId());
            }

            session.insert(STATEMENT_PREFIX + ".insertLogChanges", log.getLogChanges());
        }

        try {
            session.commit();
            session.close();
        } catch (Exception e) {
            this.log.error("Ошибка записи журнала событий в базу данных", e);
        }
    }

    public List<LogChange> getLogChanges(IStrategy strategy, DomainObject oldDomainObject, DomainObject newDomainObject) {
        final Locale systemLocale = stringLocaleBean.getSystemLocale();
        List<LogChange> logChanges = new ArrayList<LogChange>();

        if (oldDomainObject == null) {
            for (Attribute na : newDomainObject.getAttributes()) {
                EntityAttribute entityAttribute = strategy.getEntity().getAttribute(na.getEntityAttributeId());
                ValueType attributeValueType = entityAttribute.getValueType();

                if (attributeValueType.isSimple()) {
                    log(strategy, systemLocale, logChanges, na, attributeValueType);
                } else {
                    logChanges.add(new LogChange(na.getAttributeId(), null, strategy.getAttributeLabel(na, systemLocale),
                            null, StringUtil.valueOf(na.getValueId()), null));
                }
            }
        } else {
            for (Attribute oa : oldDomainObject.getAttributes()) {
                EntityAttribute oldEntityAttribute = strategy.getEntity().getAttribute(oa.getEntityAttributeId());
                ValueType oldAttributeValueType = oldEntityAttribute.getValueType();

                boolean removed = true;
                for (Attribute na : newDomainObject.getAttributes()) {
                    if (oa.getEntityAttributeId().equals(na.getEntityAttributeId()) && oa.getAttributeId().equals(na.getAttributeId())) {
                        //the same attribute_type and the same attribute_id

                        EntityAttribute newEntityAttribute = strategy.getEntity().getAttribute(na.getEntityAttributeId());
                        ValueType newAttributeValueType = newEntityAttribute.getValueType();

                        if (newAttributeValueType.isSimple() && oldAttributeValueType.isSimple()) {
                            if (ValueType.STRING_VALUE.equals(newAttributeValueType) || ValueType.STRING_VALUE.equals(oldAttributeValueType)) {
                                for (StringValue oldString : oa.getStringValues()) {
                                    for (StringValue newString : na.getStringValues()) {
                                        if (oldString.getLocaleId().equals(newString.getLocaleId())) {
                                            //compare strings
                                            if (!Strings.isEqual(oldString.getValue(), newString.getValue())) {
                                                logChanges.add(new LogChange(na.getAttributeId(), null,
                                                        strategy.getAttributeLabel(oa, systemLocale),
                                                        oldString.getValue(), newString.getValue(),
                                                        stringLocaleBean.getLocaleObject(oldString.getLocaleId()).getLanguage()));
                                            }
                                        }
                                    }
                                }
                            } else {
                                String oldValue = oa.getStringValue();
                                String newValue = na.getStringValue();

                                if (!Strings.isEqual(oldValue, newValue)) {
                                    logChanges.add(new LogChange(oa.getAttributeId(), null,
                                            strategy.getAttributeLabel(oa, systemLocale), oldValue, newValue, null));
                                }
                            }
                        } else {
                            if (!Numbers.isEqual(oa.getValueId(), na.getValueId())) {
                                logChanges.add(new LogChange(oa.getAttributeId(), null, strategy.getAttributeLabel(oa, systemLocale),
                                        StringUtil.valueOf(oa.getValueId()), StringUtil.valueOf(na.getValueId()), null));
                            }
                        }

                        removed = false;
                        break;
                    }
                }

                if (removed) {
                    if (oldAttributeValueType.isSimple()) {
                        if (ValueType.STRING_VALUE.equals(oldAttributeValueType)) {
                            for (StringValue oldString : oa.getStringValues()) {
                                if (!Strings.isEqual(oldString.getValue(), null)) {
                                    logChanges.add(new LogChange(oa.getAttributeId(), null,
                                            strategy.getAttributeLabel(oa, systemLocale),
                                            oldString.getValue(), null,
                                            stringLocaleBean.getLocaleObject(oldString.getLocaleId()).getLanguage()));
                                }
                            }
                        } else {
                            logChanges.add(new LogChange(oa.getAttributeId(), null,
                                    strategy.getAttributeLabel(oa, systemLocale),
                                    oa.getStringValue(),
                                    null, null));
                        }
                    } else {
                        logChanges.add(new LogChange(oa.getAttributeId(), null, strategy.getAttributeLabel(oa, systemLocale),
                                StringUtil.valueOf(oa.getValueId()), null, null));
                    }
                }
            }

            for (Attribute na : newDomainObject.getAttributes()) {
                EntityAttribute newEntityAttribute = strategy.getEntity().getAttribute(na.getEntityAttributeId());
                ValueType newAttributeValueType = newEntityAttribute.getValueType();

                boolean added = true;
                for (Attribute oa : oldDomainObject.getAttributes()) {
                    if (oa.getEntityAttributeId().equals(na.getEntityAttributeId()) && oa.getAttributeId().equals(na.getAttributeId())) {
                        //the same attribute_type and the same attribute_id
                        added = false;
                        break;
                    }
                }

                if (added) {
                    if (newAttributeValueType.isSimple()) {
                        log(strategy, systemLocale, logChanges, na, newAttributeValueType);
                    } else {
                        logChanges.add(new LogChange(na.getAttributeId(), null, strategy.getAttributeLabel(na, systemLocale),
                                null, String.valueOf(na.getValueId()), null));
                    }
                }
            }
        }

        return logChanges;
    }

    private void log(IStrategy strategy, Locale systemLocale, List<LogChange> logChanges, Attribute na, ValueType attributeValueType) {
        if (ValueType.STRING_VALUE.equals(attributeValueType)) {
            for (StringValue newString : na.getStringValues()) {
                if (!Strings.isEqual(newString.getValue(), null)) {
                    logChanges.add(new LogChange(na.getAttributeId(), null,
                            strategy.getAttributeLabel(na, systemLocale), null, newString.getValue(),
                            stringLocaleBean.getLocaleObject(newString.getLocaleId()).getLanguage()));
                }
            }
        } else {
            logChanges.add(new LogChange(na.getAttributeId(), null,
                    strategy.getAttributeLabel(na, systemLocale), null, na.getStringValue(), null));
        }
    }
}
