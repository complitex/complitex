package ru.complitex.common.web.component.back;

import org.apache.wicket.Component;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

import javax.servlet.http.HttpSession;

/**
 *
 * @author Artem
 */
public final class BackInfoManager {

    private BackInfoManager() {
    }

    private static HttpSession session(Component pageComponent) {
        return ((ServletWebRequest) pageComponent.getRequest()).getContainerRequest().getSession();
    }

    public static void put(Component pageComponent, String key, BackInfo backInfo) {
        session(pageComponent).setAttribute(key, backInfo);
    }

    public static BackInfo get(Component pageComponent, String key) {
        return (BackInfo) session(pageComponent).getAttribute(key);
    }
}
