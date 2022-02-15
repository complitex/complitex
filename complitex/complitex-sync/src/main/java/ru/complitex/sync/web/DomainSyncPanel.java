package ru.complitex.sync.web;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.strategy.street.StreetStrategy;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.web.component.ajax.AjaxLinkLabel;
import ru.complitex.common.web.component.datatable.Action;
import ru.complitex.common.web.component.datatable.FilteredDataTable;
import ru.complitex.common.web.component.datatable.column.EnumColumn;
import ru.complitex.common.wicket.BroadcastBehavior;
import ru.complitex.sync.entity.*;
import ru.complitex.sync.service.DomainSyncBean;
import ru.complitex.sync.service.DomainSyncService;

import javax.ejb.EJB;
import java.util.*;


/**
 * @author Anatoly Ivanov
 *         Date: 024 24.06.14 17:57
 */
public class DomainSyncPanel extends Panel {
    private Logger log = LoggerFactory.getLogger(DomainSyncPanel.class);

    private final static String[] FIELDS = {"name", "additionalName", "altName", "altAdditionalName", "parentObjectId",
            "parentId", "additionalParentId", "externalId", "additionalExternalId", "type", "status", "date"};

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private DomainSyncService domainSyncService;

    @EJB
    private StreetStrategy streetStrategy;

    public DomainSyncPanel(String id, SyncEntity syncEntity) {
        super(id);

        setOutputMarkupId(true);

        Label processed = new Label("processed", Model.of(""));
        processed.setOutputMarkupId(true);
        add(processed);

        //actions
        List<Action<DomainSync>> actions = new ArrayList<>();

        Map<String, IColumn<DomainSync, String>> columnMap = new HashMap<>();

        columnMap.put("parentObjectId", new DomainSyncParentColumn("parentObjectId"));
        columnMap.put("type", new EnumColumn<>("type", AddressEntity.class, getLocale()));
        columnMap.put("status", new EnumColumn<>("status", DomainSyncStatus.class, getLocale()));

        FilteredDataTable<DomainSync> table;

        add(table = new FilteredDataTable<DomainSync>("table", DomainSync.class, columnMap, actions, FIELDS) {
            @Override
            public List<DomainSync> getList(FilterWrapper<DomainSync> filterWrapper) {
                return domainSyncBean.getList(filterWrapper);
            }

            @Override
            public Long getCount(FilterWrapper<DomainSync> filterWrapper) {
                return domainSyncBean.getCount(filterWrapper);
            }

            @Override
            protected void onInit(FilterWrapper<DomainSync> filterWrapper) {
                filterWrapper.getObject().setType(syncEntity);
            }

            @Override
            protected boolean isVisible(String field) {
                return !(field.equals("type")) && super.isVisible(field);
            }
        });

        add(new ListView<DomainSyncFilter>("filters", new LoadableDetachableModel<List<DomainSyncFilter>>() {
            @Override
            protected List<DomainSyncFilter> load() {
                return domainSyncBean.getDomainSyncFilters(syncEntity);
            }
        }) {
            @Override
            protected void populateItem(ListItem<DomainSyncFilter> item) {
                DomainSyncFilter filter = item.getModelObject();
                String status = ResourceUtil.getString(DomainSyncStatus.class, filter.getStatus().name(), getLocale());

                item.add(new AjaxLinkLabel("filter", Model.of(status + " (" + filter.getCount() +")")) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        table.getFilterWrapper().getObject().setStatus(filter.getStatus());
                        target.add(table);
                    }
                });
            }
        });


        add(new AjaxLink("load") {
            @Override
            public boolean isVisible() {
                return !domainSyncService.isProcessing();
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
                if (domainSyncService.isProcessing()){
                    return;
                }

                getSession().info(getString("load.start"));

                setVisible(false);

                target.add(DomainSyncPanel.this);
                onUpdate(target);

                domainSyncService.load(syncEntity);
            }
        });

        add(new AjaxLink("sync") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                domainSyncService.sync(syncEntity);
            }

            @Override
            public boolean isVisible() {
                return !domainSyncService.isProcessing();
            }
        });

        add(new AjaxLink("cancel") {
            @Override
            public boolean isVisible() {
                return domainSyncService.isProcessing();
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                domainSyncService.cancelSync();
            }
        });

        add(new BroadcastBehavior(DomainSyncService.class) {
            private Long lastProcessed = System.currentTimeMillis();

            private Long index = 0L;

            @SuppressWarnings("Duplicates")
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if ("begin".equals(key)){
                    index = 0L;

                    SyncBeginMessage begin = (SyncBeginMessage) payload;

                    getSession().info(String.format(getString("load.onBegin"),
                            Objects.toString(begin.getParentName(), ""), begin.getCount()));
                    onUpdate(handler);
                }else if ("done".equals(key)){
                    getSession().info(getString("load.onDone"));

                    processed.setDefaultModelObject("");
                    onUpdate(handler);

                    handler.add(DomainSyncPanel.this);
                    handler.add(processed);
                }else if ("error".equals(key)){
                    getSession().error(Objects.toString(payload));

                    onUpdate(handler);
                    handler.add(DomainSyncPanel.this);
                }else if ("info".equals(key)){
                    getSession().info(Objects.toString(payload));

                    onUpdate(handler);
                    handler.add(DomainSyncPanel.this);
                }

                if (payload instanceof DomainSync) {
                    index++;

                    if (System.currentTimeMillis() - lastProcessed > 250) {
                        String message = index + ", " + ((DomainSync) payload).getName();

                        processed.setDefaultModelObject(message);

                        handler.add(processed);

                        lastProcessed = System.currentTimeMillis();
                    }
                }
            }
        });
    }

    protected void onUpdate(IPartialPageRequestHandler target){
    }
}
