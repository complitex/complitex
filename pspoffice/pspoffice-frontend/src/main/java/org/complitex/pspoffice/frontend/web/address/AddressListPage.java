package org.complitex.pspoffice.frontend.web.address;

import com.google.common.collect.Sets;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.AddressObject;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov
 * 20.07.2017 12:37
 */
public class AddressListPage extends BasePage{
    private static final Set<String> ENTITIES = Sets.newHashSet(
            "country", "region", "city", "district","street", "building", "apartment"
    );

    private String entity;

    public AddressListPage(PageParameters pageParameters) {
        entity = pageParameters.get("entity").toString();

        if (entity == null || !ENTITIES.contains(entity)){
            throw new RuntimeException("wrong address entity");
        }

        add(new TablePanel<>("addresses", AddressObject.class, Arrays.asList("objectId", "name.ru"),
                new AddressDataProvider(entity)));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(entity);
    }
}
