package org.complitex.resources.theme;


import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.05.11 16:02
 */
public class ThemeResourceReference extends CssResourceReference {
    public ThemeResourceReference() {
        super(ThemeResourceReference.class, "jquery-ui-1.10.4.custom.css");
    }
}
