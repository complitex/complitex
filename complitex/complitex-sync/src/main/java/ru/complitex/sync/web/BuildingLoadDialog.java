package ru.complitex.sync.web;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.web.component.AddressSearchComponent;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.web.component.search.SearchComponentState;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.HashMap;
import java.util.Map;

/**
 * @author inheaven on 021 21.12.15 18:41
 */
public class BuildingLoadDialog extends Panel{
    private Dialog dialog;

    public BuildingLoadDialog(String id) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(550);
        dialog.setTitle(new ResourceModel("building_load_dialog_title"));
        add(dialog);

        SearchComponentState districtState = new SearchComponentState();
        SearchComponentState streetState = new SearchComponentState();

        dialog.add(new AddressSearchComponent("district", districtState, ImmutableList.of("city", "district")));
        dialog.add(new AddressSearchComponent("street", streetState, ImmutableList.of("street")));

        dialog.add(new AjaxLink("load") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Map<String, DomainObject> map = new HashMap<>();
                map.putAll(districtState);
                map.putAll(streetState);

                onLoad(target, map);
                dialog.close(target);
            }
        });

        dialog.add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        });
    }

    public void open(AjaxRequestTarget target){
        dialog.open(target);
    }

    protected void onLoad(AjaxRequestTarget target, Map<String, DomainObject> map){
    }
}
