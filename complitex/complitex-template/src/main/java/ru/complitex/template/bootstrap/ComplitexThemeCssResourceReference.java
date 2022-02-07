package ru.complitex.template.bootstrap;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author inheaven on 003 03.11.15 16:29
 */
public class ComplitexThemeCssResourceReference extends CssResourceReference{
    public static final ComplitexThemeCssResourceReference INSTANCE = new ComplitexThemeCssResourceReference();

    public ComplitexThemeCssResourceReference() {
        super(ComplitexThemeCssResourceReference.class, "css/bootstrap.complitex.css");
    }
}
