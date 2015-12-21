package org.complitex.address.web.sync;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.web.component.AddressSearchComponent;
import org.complitex.common.web.component.search.SearchComponentState;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.List;

/**
 * @author inheaven on 013 13.08.15 17:19
 */
public class AddressSyncDialog extends Panel{
    private Dialog dialog;

    public AddressSyncDialog(String id, AddressEntity addressEntity) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(600);
        dialog.setTitle(new ResourceModel("address_sync_dialog_title"));
        add(dialog);

        SearchComponentState state = new SearchComponentState();

        dialog.add(new AddressSearchComponent("search", state, getFilters(addressEntity)){
            @Override
            public boolean isVisible() {
                return addressEntity.equals(AddressEntity.BUILDING) || addressEntity.equals(AddressEntity.STREET);
            }
        });

        dialog.add(new AjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAdd(target, state);
                dialog.close(target);
            }
        });

        dialog.add(new AjaxLink("update") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onUpdate(target, state);
                dialog.close(target);
            }
        });

        dialog.add(new AjaxLink("delete") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onDelete(target, state);
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

    public void onAdd(AjaxRequestTarget target, SearchComponentState state){
    }

    public void onUpdate(AjaxRequestTarget target, SearchComponentState state){
    }

    public void onDelete(AjaxRequestTarget target, SearchComponentState state){
    }

    protected List<String> getFilters(AddressEntity addressEntity) {
        switch (addressEntity) {
            case STREET:
                return ImmutableList.of("city");
            case BUILDING:
                return ImmutableList.of("street");
        }

        return ImmutableList.of("city");
    }
}
