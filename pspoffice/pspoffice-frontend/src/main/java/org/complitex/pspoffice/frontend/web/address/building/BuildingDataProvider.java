package org.complitex.pspoffice.frontend.web.address.building;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.BuildingObject;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 21.07.2017 16:26
 */
public class BuildingDataProvider extends TableDataProvider<BuildingObject> {
    @Inject
    private PspOfficeClient pspOfficeClient;

    private BuildingObject buildingObject;

    @Override
    public BuildingObject getFilterState() {
        return buildingObject;
    }

    @Override
    public void setFilterState(BuildingObject buildingObject) {
        this.buildingObject = buildingObject;
    }

    @Override
    public Iterator<? extends BuildingObject> iterator(long first, long count) {
        return pspOfficeClient.target()
                .path("address/building")
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<BuildingObject>>(){}).iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.target()
                .path("address/building/size")
                .request(APPLICATION_JSON_TYPE)
                .get(Long.class);
    }

    @Override
    public IModel<BuildingObject> model(BuildingObject buildingObject) {
        return new CompoundPropertyModel<>(buildingObject);
    }
}
