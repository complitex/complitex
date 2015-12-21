package org.complitex.address.web.sync;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.address.web.component.AddressSearchComponent;
import org.complitex.common.web.component.search.SearchComponentState;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 * @author inheaven on 021 21.12.15 18:41
 */
public class BuildingLoadDialog extends Panel{
    private Dialog dialog;

    public BuildingLoadDialog(String id) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(500);
        dialog.setTitle(new ResourceModel("building_load_dialog_title"));
        add(dialog);

        SearchComponentState state = new SearchComponentState();

        dialog.add(new AddressSearchComponent("search", state, ImmutableList.of("street")));

        dialog.add(new AjaxLink("load") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onLoad(target, state);
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

    protected void onLoad(AjaxRequestTarget target, SearchComponentState state){
    }
}
