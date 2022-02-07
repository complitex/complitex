package ru.complitex.common.web.component.image;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 * @author Artem
 */
public class StaticImage extends Image {

    public StaticImage(String id, ResourceReference resourceReference) {
        super(id, resourceReference);
    }

    @Override
    protected boolean shouldAddAntiCacheParameter() {
        return false;
    }
}
