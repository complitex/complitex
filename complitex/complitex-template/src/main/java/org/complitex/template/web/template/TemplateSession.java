package org.complitex.template.web.template;

import org.apache.wicket.request.Request;
import org.complitex.common.web.DictionaryFwSession;
import org.complitex.common.web.ISessionStorage;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.07.2010 17:16:53
 */
public class TemplateSession extends DictionaryFwSession {

    public TemplateSession(Request request, ISessionStorage sessionStorage) {
        super(request, sessionStorage);
    }
}
