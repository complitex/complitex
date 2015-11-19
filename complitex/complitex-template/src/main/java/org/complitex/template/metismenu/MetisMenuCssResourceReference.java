package org.complitex.template.metismenu;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author inheaven on 019 19.11.15 17:40
 */
public class MetisMenuCssResourceReference extends CssResourceReference{
    public static final MetisMenuCssResourceReference INSTANCE = new MetisMenuCssResourceReference();

    public MetisMenuCssResourceReference() {
        super(MetisMenuCssResourceReference.class, "metisMenu.css");
    }
}
