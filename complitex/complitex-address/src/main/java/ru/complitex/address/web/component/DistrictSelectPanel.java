package ru.complitex.address.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.address.strategy.district.DistrictStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.util.AttributeUtil;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.model.AttributeExampleModel;

import javax.ejb.EJB;
import java.util.List;

import static ru.complitex.address.strategy.district.DistrictStrategy.CODE;
import static ru.complitex.address.strategy.district.DistrictStrategy.NAME;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.01.14 1:53
 */
public class DistrictSelectPanel extends Panel {
    @EJB
    private DistrictStrategy districtStrategy;

    public DistrictSelectPanel(String id, IModel<List<DomainObject>> districtModel) {
        super(id);

        final DomainObjectFilter example = new DomainObjectFilter().addAttributes(NAME, CODE);

        final Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        form.add(new TextField<>("name", new AttributeExampleModel(example, NAME)));
        form.add(new TextField<>("code", new AttributeExampleModel(example, CODE)));

        CheckGroup checkGroup = new CheckGroup<>("check_group", districtModel);
        form.add(checkGroup);

        DataProvider<DomainObject> dataProvider = new DataProvider<DomainObject>() {
            @Override
            protected Iterable<? extends DomainObject> getData(long first, long count) {
                return districtStrategy.find(example, first, count);
            }

            @Override
            protected Long getSize() {
                return districtStrategy.getCount(example);
            }
        };

        DataView<DomainObject> dataView = new DataView<DomainObject>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<DomainObject> item) {
                final DomainObject domainObject = item.getModelObject();

                item.add(new Check<>("check", Model.of(domainObject)));
                item.add(new Label("name", domainObject.getStringValue(NAME, getLocale())));
                item.add(new Label("code", AttributeUtil.getStringValue(domainObject, CODE)));
            }
        };
        checkGroup.add(dataView);

        form.add(new PagingNavigator("navigator", dataView, form));

        form.add(new AjaxButton("filter") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        });
    }
}
