package ru.complitex.pspoffice.frontend.web.address.building;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.pspoffice.frontend.web.BasePage;
import ru.complitex.pspoffice.frontend.web.component.datatable.TablePanel;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 21.07.2017 16:31
 */
public class BuildingListPage extends BasePage{
    @Inject
    private BuildingDataProvider buildingDataProvider;

    public BuildingListPage() {
        add(new TablePanel<>("buildings", Arrays.asList("id", "number.ru"), buildingDataProvider));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
