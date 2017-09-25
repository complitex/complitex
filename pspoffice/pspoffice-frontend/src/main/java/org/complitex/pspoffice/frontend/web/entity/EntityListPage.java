package org.complitex.pspoffice.frontend.web.entity;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.EntityModel;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 21.09.2017 15:50
 */
public class EntityListPage extends BasePage{
    @Inject
    private EntityDataProvider entityDataProvider;

    public EntityListPage() {
        add(new NotificationPanel("feedback"));

        add(new TablePanel<EntityModel>("entities", EntityModel.class,
                Arrays.asList("id", "entity", "names.1", "names.2"), entityDataProvider, EntityEditPage.class){
            @Override
            protected void populateEdit(IModel<EntityModel> rowModel, PageParameters pageParameters) {
                pageParameters.add("id", rowModel.getObject().getId());
                pageParameters.add("entity", rowModel.getObject().getEntity());
            }
        });
    }

    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
