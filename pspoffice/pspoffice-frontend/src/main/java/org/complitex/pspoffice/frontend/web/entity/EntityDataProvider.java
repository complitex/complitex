package org.complitex.pspoffice.frontend.web.entity;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.entity.Entity;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.ui.wicket.datatable.TableDataProvider;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 21.09.2017 15:45
 */
public class EntityDataProvider extends TableDataProvider<Entity>{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private Entity entity = new Entity();

    @Override
    public Entity getFilterState() {
        return entity;
    }

    @Override
    public void setFilterState(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Iterator<? extends Entity> iterator(long first, long count) {
        return pspOfficeClient.target()
                .path("entity")
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<Entity>>(){})
                .iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.request("entity/size").get(Long.class);
    }

    @Override
    public IModel<Entity> model(Entity entity) {
        return new CompoundPropertyModel<>(entity);
    }
}
