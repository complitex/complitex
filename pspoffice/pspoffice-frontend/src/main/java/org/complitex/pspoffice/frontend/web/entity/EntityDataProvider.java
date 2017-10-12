package org.complitex.pspoffice.frontend.web.entity;

import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.EntityModel;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 21.09.2017 15:45
 */
public class EntityDataProvider extends TableDataProvider<EntityModel>{
    @Inject
    private PspOfficeClient pspOfficeClient;

    @Override
    public Iterator<? extends EntityModel> iterator(long first, long count) {
        return pspOfficeClient.target()
                .path("entity")
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<EntityModel>>(){})
                .iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.request("entity/size").get(Long.class);
    }
}
