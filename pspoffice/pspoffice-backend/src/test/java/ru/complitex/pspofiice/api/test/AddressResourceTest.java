package ru.complitex.pspofiice.api.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Anatoly A. Ivanov
 *         02.05.2017 16:30
 */
public class AddressResourceTest {
    private WebTarget target;

    @Before
    public void before(){
        Client client = ClientBuilder.newClient();

        target = client.target("http://localhost:8080").path("/pspoffice/api/");
    }

    @Test
    public void testPing(){
        Response response = target.path("address/ping").request(APPLICATION_JSON).get();

        Assert.assertEquals("ping", response.readEntity(String.class));
    }
}
