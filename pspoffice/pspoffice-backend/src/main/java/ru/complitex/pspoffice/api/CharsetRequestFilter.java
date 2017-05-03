package ru.complitex.pspoffice.api;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov
 *         03.05.2017 18:59
 */
@Provider
public class CharsetRequestFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        MediaType type = response.getMediaType();

        if (type != null) {
            String contentType = type.toString();
            if (!contentType.contains("charset")) {
                contentType = contentType + "; charset=utf-8";
                response.getHeaders().putSingle("Content-Type", contentType);
            }
        }
    }
}
