package ru.complitex.template.web.template;

import org.apache.wicket.request.Request;
import ru.complitex.common.web.DictionaryFwSession;
import ru.complitex.common.web.ISessionStorage;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.07.2010 17:16:53
 */
public class TemplateSession extends DictionaryFwSession {

    public TemplateSession(Request request, ISessionStorage sessionStorage) {
        super(request, sessionStorage);
    }
}
