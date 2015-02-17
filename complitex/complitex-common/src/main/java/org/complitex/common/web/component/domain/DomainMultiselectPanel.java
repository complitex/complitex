package org.complitex.common.web.component.domain;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.StrategyFactory;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 013 13.02.15 16:47
 */
public class DomainMultiselectPanel extends Panel {
    @EJB
    private StrategyFactory strategyFactory;

    public DomainMultiselectPanel(String id, final String entityTable, final IModel<List<DomainObject>> model, IModel<String> titleModel) {
        super(id);

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        final DomainSelectDialog domainSelectDialog = new DomainSelectDialog("domainSelectDialog", "service", titleModel){
            @Override
            protected void onSelect(AjaxRequestTarget target) {
                target.add(container);
            }
        };
        add(domainSelectDialog);

        ListView<DomainObject> listView = new ListView<DomainObject>("listView", model) {
            @Override
            protected void populateItem(final ListItem<DomainObject> item) {
                final DomainObject domainObject = item.getModelObject();

                String name = domainObject.getObjectId() != null
                        ? strategyFactory.getStrategy(entityTable).displayDomainObject(domainObject, getLocale())
                        : getString("not_selected");

                item.add(new Label("name", Model.of(name)));
                item.add(new AjaxLink("select") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        domainSelectDialog.open(target, item.getModel());
                    }
                });
                item.add(new AjaxLink("delete") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        model.getObject().remove(domainObject);

                        target.add(container);
                    }
                });
            }
        };
        container.add(listView);

        container.add(new AjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                model.getObject().add(new DomainObject());

                target.add(container);
            }
        });
    }


}
