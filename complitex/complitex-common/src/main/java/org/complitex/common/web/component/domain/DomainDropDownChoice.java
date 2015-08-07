package org.complitex.common.web.component.domain;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.util.EjbBeanLocator;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author inheaven on 019 19.05.15 16:06
 */
public class DomainDropDownChoice extends DropDownChoice<Long>{
    private String entityName;

    public DomainDropDownChoice(String id, String entityName, IModel<Long> model) {
        super(id, model, Collections.emptyList());

        this.entityName = entityName;

        DomainObjectFilter filter = new DomainObjectFilter();
        onFilter(filter);

        setChoices(getStrategy().getList(filter).stream().map(DomainObject::getId).collect(Collectors.toList()));

        setChoiceRenderer(new IChoiceRenderer<Long>() {
            @Override
            public Object getDisplayValue(Long objectId) {
                return getStrategy().displayDomainObject(objectId, getLocale());
            }

            @Override
            public String getIdValue(Long object, int index) {
                return object.toString();
            }
        });
    }

    protected IStrategy getStrategy(){
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entityName);
    }

    protected void onFilter(DomainObjectFilter filter){
    }
}