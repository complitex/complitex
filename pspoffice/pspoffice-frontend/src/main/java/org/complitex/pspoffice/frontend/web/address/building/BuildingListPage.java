package org.complitex.pspoffice.frontend.web.address.building;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.BuildingObject;

import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 21.07.2017 16:31
 */
public class BuildingListPage extends BasePage{
    public BuildingListPage() {
        add(new TablePanel<>("buildings", BuildingObject.class, Arrays.asList("objectId", "numbers[0].name"),
                new BuildingDataProvider()));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
