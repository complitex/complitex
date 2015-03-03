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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StrategyFactory;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 013 13.02.15 16:47
 */
public abstract class DomainMultiselectPanel<T> extends Panel {
    @EJB
    private StrategyFactory strategyFactory;

    private String entityName;

    public DomainMultiselectPanel(String id, final String entityName, final IModel<List<T>> model,
                                  final String objectIdExpression) {
        super(id);

        this.entityName = entityName;

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        final DomainSelectDialog domainSelectDialog = new DomainSelectDialog("domainSelectDialog", entityName,
                new ResourceModel(id)){
            @Override
            protected void onSelect(AjaxRequestTarget target) {
                target.add(container);
            }
        };
        add(domainSelectDialog);

        ListView<T> listView = new ListView<T>("listView", model) {
            @Override
            protected void populateItem(final ListItem<T> item) {
                final IModel<Long> objectIdModel = new PropertyModel<>(item.getModel(), objectIdExpression);

                item.add(new Label("name", Model.of(displayDomainObject(objectIdModel.getObject()))));
                item.add(new AjaxLink("select") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        filter(domainSelectDialog.getFilter());

                        domainSelectDialog.open(target, objectIdModel);
                    }
                });
                item.add(new AjaxLink("delete") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        model.getObject().remove(item.getModelObject());

                        target.add(container);
                    }
                });
            }
        };
        container.add(listView);

        container.add(new AjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                model.getObject().add(newModelObject());

                target.add(container);
            }
        });
    }

    protected abstract T newModelObject();

    protected void filter(DomainObjectFilter filter){
    }

    protected String displayDomainObject(Long domainObjectId){
        return domainObjectId != null
                ? strategyFactory.getStrategy(entityName).displayDomainObject(domainObjectId, getLocale())
                : getString("not_selected");
    }
}
