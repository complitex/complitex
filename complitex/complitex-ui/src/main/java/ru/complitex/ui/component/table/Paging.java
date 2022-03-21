package ru.complitex.ui.component.table;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigatorLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Ivanov Anatoliy
 */
public class Paging extends Panel {
    public static final MetaDataKey<HashMap<String, Long>> ITEMS_PER_PAGE = new MetaDataKey<HashMap<String, Long>>() {};

    public Paging(String id, IPageableItems pageableItems, String tableKey) {
        super(id);

        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");

        add(container);

        container.add(newComponent("component"));
        container.add(new NavigatorLabel("label", pageableItems));
        container.add(new BootstrapAjaxPagingNavigator("navigator", pageableItems).setSize(BootstrapPagingNavigator.Size.Small));

        HashMap<String, Long> map = getSession().getMetaData(ITEMS_PER_PAGE);

        if(map == null){
            map = new HashMap<>();

            getSession().setMetaData(ITEMS_PER_PAGE, map);
        }

        Long itemsPerPages = map.get(tableKey);

        if (itemsPerPages == null){
            itemsPerPages = pageableItems.getItemsPerPage();

            map.put(tableKey, itemsPerPages);
        }else{
            pageableItems.setItemsPerPage(itemsPerPages);
        }

        IModel<Long> itemsPerPageModel = Model.of(itemsPerPages);

        container.add(new DropDownChoice<>("size", itemsPerPageModel, Arrays.asList(5L, 10L, 15L, 20L, 25L, 50L, 100L))
                .add(OnChangeAjaxBehavior.onChange(target -> {
                    pageableItems.setItemsPerPage(itemsPerPageModel.getObject());

                    getSession().getMetaData(ITEMS_PER_PAGE).put(tableKey, itemsPerPageModel.getObject());

                    Paging.this.onChange(target);
                })));
    }

    protected Component newComponent(String componentId){
        return new EmptyPanel(componentId);
    }

    protected void onChange(AjaxRequestTarget target) {}
}
