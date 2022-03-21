package ru.complitex.pspoffice.frontend.web.domain;

import ru.complitex.pspoffice.frontend.service.PspOfficeClient;
import ru.complitex.pspoffice.frontend.web.component.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.DomainModel;

import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 12.10.2017 15:18
 */
public class DomainDataProvider extends TableDataProvider<DomainModel>{
    private PspOfficeClient pspOfficeClient;

    private String entity;

    public DomainDataProvider(PspOfficeClient pspOfficeClient, String entity) {
        this.pspOfficeClient = pspOfficeClient;
        this.entity = entity;
    }

    @Override
    public Iterator<? extends DomainModel> iterator(long first, long count) {
        return pspOfficeClient.target()
                .path("domain/" + entity)
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<DomainModel>>(){})
                .iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.request("domain/" + entity + "/size").get(Long.class);
    }
}
