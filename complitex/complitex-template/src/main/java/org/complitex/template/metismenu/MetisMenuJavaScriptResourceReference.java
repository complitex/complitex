package org.complitex.template.metismenu;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author inheaven on 019 19.11.15 17:42
 */
public class MetisMenuJavaScriptResourceReference extends JavaScriptResourceReference{
    public static final MetisMenuJavaScriptResourceReference INSTANCE = new MetisMenuJavaScriptResourceReference();

    public MetisMenuJavaScriptResourceReference() {
        super(MetisMenuJavaScriptResourceReference.class, "metisMenu.js");
    }
}
