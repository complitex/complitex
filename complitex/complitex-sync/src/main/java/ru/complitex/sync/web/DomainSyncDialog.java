package ru.complitex.sync.web;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.web.component.AddressSearchComponent;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.sync.entity.SyncEntity;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.List;

import static ru.complitex.sync.entity.SyncEntity.BUILDING;
import static ru.complitex.sync.entity.SyncEntity.STREET;

/**
 * @author inheaven on 013 13.08.15 17:19
 */
public class DomainSyncDialog extends Panel{
    private Dialog dialog;

    public DomainSyncDialog(String id, SyncEntity syncEntity) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(350);
        dialog.setTitle(new ResourceModel("address_sync_dialog_title"));
        add(dialog);

        SearchComponentState state = new SearchComponentState();

        dialog.add(new AddressSearchComponent("search", state, getFilters(syncEntity)){
            @Override
            public boolean isVisible() {
                return syncEntity.equals(BUILDING) || syncEntity.equals(STREET);
            }
        });

        dialog.add(new AjaxLink("update") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onUpdate(target, state);

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

    public void onUpdate(AjaxRequestTarget target, SearchComponentState state){
    }

    private List<String> getFilters(SyncEntity syncEntity) {
        switch (syncEntity) {
            case STREET:
                return ImmutableList.of("city");
            case BUILDING:
                return ImmutableList.of("street");
        }

        return ImmutableList.of("city");
    }
}
