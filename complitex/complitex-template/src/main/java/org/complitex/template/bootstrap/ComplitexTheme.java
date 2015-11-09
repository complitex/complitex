package org.complitex.template.bootstrap;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.Theme;

/**
 * @author inheaven on 003 03.11.15 16:28
 */
public class ComplitexTheme extends Theme{
    public ComplitexTheme() {
        super("complitex", ComplitexThemeCssResourceReference.INSTANCE, Bootstrap.getSettings().getCssResourceReference());
    }
}
