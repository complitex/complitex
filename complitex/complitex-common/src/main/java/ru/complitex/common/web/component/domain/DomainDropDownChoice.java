package ru.complitex.common.web.component.domain;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;

import java.util.Collections;
import java.util.List;
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

            @Override
            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                return Long.valueOf(id);
            }
        });
    }

    protected IStrategy getStrategy(){
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entityName);
    }

    protected void onFilter(DomainObjectFilter filter){
    }
}
