package ru.complitex.pspoffice.address.sync.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.util.Dates;
import ru.complitex.catalog.util.Threads;
import ru.complitex.pspoffice.address.catalog.entity.*;
import ru.complitex.pspoffice.address.correction.entity.*;
import ru.complitex.pspoffice.address.page.BasePage;
import ru.complitex.pspoffice.address.sync.entity.Sync;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;
import ru.complitex.pspoffice.address.sync.page.component.CatalogPanel;
import ru.complitex.pspoffice.address.sync.page.component.SyncModal;
import ru.complitex.pspoffice.address.sync.page.component.SyncPanel;
import ru.complitex.pspoffice.address.sync.service.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ivanov Anatoliy
 */
public class SyncPage extends BasePage {
    private final static Logger log = LoggerFactory.getLogger(SyncPage.class);

    @Inject
    private CountrySyncService countrySyncService;

    @Inject
    private RegionSyncService regionSyncService;

    @Inject
    private CityTypeSyncService cityTypeSyncService;

    @Inject
    private CitySyncService citySyncService;

    @Inject
    private DistrictSyncService districtSyncService;

    @Inject
    private StreetTypeSyncService streetTypeSyncService;

    @Inject
    private StreetSyncService streetSyncService;

    @Inject
    private HouseSyncService houseSyncService;

    @Inject
    private FlatSyncService flatSyncService;

    private final Map<Integer, SyncPanel> map = new HashMap<>();

    private final AtomicBoolean status = new AtomicBoolean(false);

    WebMarkupContainer buttons;

    private static record SyncMessage(int catalog, String message) implements IWebSocketPushMessage {}

    private final transient Application application = getApplication();
    private final transient Session session = getSession();

    private final LocalDate date = Dates.now();

    public SyncPage() {
        NotificationPanel notification = new NotificationPanel("notification");
        notification.setOutputMarkupId(true);
        add(notification);

        CatalogPanel country = new CatalogPanel("country", Country.CATALOG, CountryCorrection.CATALOG,
                CountryCorrection.CATALOG_ORGANIZATION, CountryCorrection.CORRECTION_ORGANIZATION,
                CountryCorrection.COUNTRY_ID, date);
        add(country, Country.CATALOG);

        CatalogPanel region = new CatalogPanel("region", Region.CATALOG, RegionCorrection.CATALOG,
                RegionCorrection.CATALOG_ORGANIZATION, RegionCorrection.CORRECTION_ORGANIZATION,
                RegionCorrection.REGION_ID, date);
        add(region, Region.CATALOG);

        CatalogPanel cityType = new CatalogPanel("cityType", CityType.CATALOG, CityTypeCorrection.CATALOG,
                CityTypeCorrection.CATALOG_ORGANIZATION, CityTypeCorrection.CORRECTION_ORGANIZATION,
                CityTypeCorrection.CITY_TYPE_ID, date);
        add(cityType, CityType.CATALOG);

        CatalogPanel city = new CatalogPanel("city", City.CATALOG, CityCorrection.CATALOG,
                CityCorrection.CATALOG_ORGANIZATION, CityCorrection.CORRECTION_ORGANIZATION,
                CityCorrection.CITY_ID, date);
        add(city, City.CATALOG);

        CatalogPanel district = new CatalogPanel("district", District.CATALOG, DistrictCorrection.CATALOG,
                DistrictCorrection.CATALOG_ORGANIZATION, DistrictCorrection.CORRECTION_ORGANIZATION,
                DistrictCorrection.DISTRICT_ID, date);
        add(district, District.CATALOG);

        CatalogPanel streetType = new CatalogPanel("streetType", StreetType.CATALOG, StreetTypeCorrection.CATALOG,
                StreetTypeCorrection.CATALOG_ORGANIZATION, StreetTypeCorrection.CORRECTION_ORGANIZATION,
                StreetTypeCorrection.STREET_TYPE_ID, date);
        add(streetType, StreetType.CATALOG);

        CatalogPanel street = new CatalogPanel("street", Street.CATALOG, StreetCorrection.CATALOG,
                StreetCorrection.CATALOG_ORGANIZATION, StreetCorrection.CORRECTION_ORGANIZATION,
                StreetCorrection.STREET_TYPE_ID, date);
        add(street, Street.CATALOG);

        CatalogPanel house = new CatalogPanel("house", House.CATALOG, HouseCorrection.CATALOG,
                HouseCorrection.CATALOG_ORGANIZATION, HouseCorrection.CORRECTION_ORGANIZATION,
                HouseCorrection.HOUSE_ID, date);
        add(house, House.CATALOG);

        CatalogPanel flat = new CatalogPanel("flat", Flat.CATALOG, FlatCorrection.CATALOG,
                FlatCorrection.CATALOG_ORGANIZATION, FlatCorrection.CORRECTION_ORGANIZATION,
                FlatCorrection.FLAT_ID, date);
        add(flat, Flat.CATALOG);

        add(new WebSocketBehavior() {
            long time = System.currentTimeMillis();

            @Override
            protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
                if (message instanceof SyncMessage) {
                    int catalog = ((SyncMessage) message).catalog();

                    if (catalog == 0) {
                        success(getString("synced"));

                        handler.add(notification, buttons);

                        return;
                    }

                    if (catalog == -1) {
                        error(getString("error"));

                        handler.add(notification, buttons);

                        return;
                    }

                    SyncPanel syncRow = map.get(catalog);

                    switch (((SyncMessage) message).message()){
                        case "onSync" -> syncRow.onLoad();
                        case "onAdd" -> syncRow.onAdd();
                        case "onUpdate" -> syncRow.onUpdate();
                        case "onAddCorrection" -> syncRow.onAddCorrection();
                        case "onUpdateCorrection" -> syncRow.onUpdateCorrection();
                        case "onError" -> syncRow.onError();
                        case "onSynced" -> handler.add(syncRow);
                    }

                    if (System.currentTimeMillis() - time > 100) {
                        handler.add(syncRow);

                        time = System.currentTimeMillis();
                    }
                }
            }
        });

        ISyncListener<SyncCatalog> listener =  new ISyncListener<>() {
            @Override
            public void onSync(int catalog, Sync sync) {
                sendMessage(catalog, "onSync");
            }

            @Override
            public void onAdd(int catalog, Item item, Sync sync) {
                sendMessage(catalog, "onAdd");
            }

            @Override
            public void onAddCorrection(int catalog, Item correction, Sync sync) {
                sendMessage(catalog, "onAddCorrection");
            }

            @Override
            public void onUpdate(int catalog, Item item, Sync sync) {
                sendMessage(catalog, "onUpdate");
            }

            @Override
            public void onUpdateCorrection(int catalog, Item correction, Sync sync) {
                sendMessage(catalog, "onUpdateCorrection");
            }

            @Override
            public void onDelete(int catalog, Item item, Sync sync) {
                sendMessage(catalog, "onDelete");
            }

            @Override
            public void onDeleteCorrection(int catalog, Item correction, Sync sync) {
                sendMessage(catalog, "onDeleteCorrection");
            }

            @Override
            public void onError(int catalog, SyncCatalog syncCatalog, Item item, Item correction, Sync sync, Exception exception) {
                sendMessage(catalog, "onError");
            }

            @Override
            public void onSynced(int catalog) {
                sendMessage(catalog, "onSynced");
            }
        };

        Form<?> form = new Form<>("form");
        add(form);

        SyncModal modal = new SyncModal("modal"){
            @Override
            protected void sync(AjaxRequestTarget target, List<Integer> select, LocalDate date) {
                status.set(true);

                select.forEach(catalog -> map.get(catalog).init());

                Threads.submit(() -> {
                    try {
                        select.forEach(catalog -> {
                            switch (catalog) {
                                case Country.CATALOG -> countrySyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case Region.CATALOG -> regionSyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case CityType.CATALOG -> cityTypeSyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case City.CATALOG -> citySyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case District.CATALOG -> districtSyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case StreetType.CATALOG -> streetTypeSyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case Street.CATALOG -> streetSyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case House.CATALOG -> houseSyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                                case Flat.CATALOG -> flatSyncService.sync(catalog, date, Locale.SYSTEM, listener, status);
                            }
                        });

                        status.set(false);

                        sendMessage(0, "onSynchronized");
                    } catch (Throwable t) {
                        status.set(false);

                        sendMessage(-1, "onError");

                        t.printStackTrace();
                    }
                });

                target.add(buttons);
            }
        };

        form.add(modal);

        buttons = new WebMarkupContainer("buttons");
        buttons.setOutputMarkupId(true);
        form.add(buttons);

        AjaxLink<?> sync = new AjaxLink<Void>("sync") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.open(target);

                target.add(buttons);
            }

            @Override
            public boolean isEnabled() {
                return !status.get();
            }
        };
        buttons.add(sync);

        AjaxLink<?> cancel = new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                status.set(false);

                target.add(buttons);
            }

            @Override
            public boolean isVisible() {
                return status.get();
            }
        };
        buttons.add(cancel);
    }

    public void add(SyncPanel syncRow, int catalog) {
        add(syncRow);

        map.put(catalog, syncRow);
    }

    private void sendMessage(IWebSocketPushMessage message) {
        try {
            if (session.getId() != null) {
                IWebSocketConnection webSocketConnection = WebSocketSettings.Holder.get(application)
                        .getConnectionRegistry()
                        .getConnection(application, session.getId(), new PageIdKey(getPageId()));

                if (webSocketConnection != null) {
                    webSocketConnection.sendMessage(message);
                }
            }
        } catch (Exception e) {
            log.error("error sendMessage ", e);
        }
    }

    private void sendMessage(int catalog, String message) {
        sendMessage(new SyncMessage(catalog, message));
    }
}
