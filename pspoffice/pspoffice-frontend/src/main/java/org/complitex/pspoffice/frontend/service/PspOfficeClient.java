package org.complitex.pspoffice.frontend.service;

import org.glassfish.jersey.jackson.JacksonFeature;
import ru.complitex.pspoffice.api.json.ObjectMapperProvider;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 25.07.2017 14:06
 */
public class PspOfficeClient implements Serializable{
    @Inject
    private ServletContext servletContext;

    public WebTarget target(){
        return ClientBuilder.newClient()
                .register(JacksonFeature.class)
                .register(ObjectMapperProvider.class)
                .target(servletContext.getInitParameter("backend-api"));
    }

    public Invocation.Builder request(String path){
        return target()
                .path(path)
                .request(MediaType.APPLICATION_JSON_TYPE);
    }
}
