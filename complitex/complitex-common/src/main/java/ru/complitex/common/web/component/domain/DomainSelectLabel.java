package ru.complitex.common.web.component.domain;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StrategyFactory;

import javax.ejb.EJB;

/**
 * @author inheaven on 18.03.2015 3:08.
 */
public class DomainSelectLabel extends Panel {
    @EJB
    private StrategyFactory strategyFactory;

    private String entityName;

    public DomainSelectLabel(String id, String entityName, final IModel<Long> objectIdModel) {
        super(id, objectIdModel);

        this.entityName = entityName;

        final Label name = new Label("name", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return displayDomainObject(objectIdModel.getObject());
            }
        });
        name.setOutputMarkupId(true);
        add(name);

        DomainSelectDialog domainSelectDialog = new DomainSelectDialog("domainSelectDialog", entityName,
                new ResourceModel(entityName)){
            @Override
            protected void onSelect(AjaxRequestTarget target) {
                target.add(name);
            }
        };
        add(domainSelectDialog);

        add(new AjaxLink("select") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                filter(domainSelectDialog.getFilter());

                domainSelectDialog.open(target, objectIdModel);
            }
        });
    }

    protected String displayDomainObject(Long domainObjectId){
        return domainObjectId != null && domainObjectId > 0
                ? strategyFactory.getStrategy(entityName).displayDomainObject(domainObjectId, getLocale())
                : getString("not_selected");
    }

    protected void filter(DomainObjectFilter filter){
    }


}
