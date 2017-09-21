package org.complitex.pspoffice.frontend.web.entity;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.entity.Entity;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;

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

        add(new TablePanel<>("entities", Entity.class,
                Arrays.asList("", "", "", "", ""), entityDataProvider));
    }

    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
