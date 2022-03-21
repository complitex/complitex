package ru.complitex.ui.resource;

import de.agilecoders.wicket.core.markup.html.themes.bootstrap.BootstrapCssReference;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;

import java.util.Collections;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class CssResourceReference extends org.apache.wicket.request.resource.CssResourceReference {
    public final static CssResourceReference INSTANCE = new CssResourceReference();

    private CssResourceReference() {
        super(CssResourceReference.class, "css/complitex-catalog.css");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Collections.singletonList(CssReferenceHeaderItem.forReference(BootstrapCssReference.instance()));
    }
}
