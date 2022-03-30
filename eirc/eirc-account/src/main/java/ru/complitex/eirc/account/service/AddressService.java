package ru.complitex.eirc.account.service;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import ru.complitex.sync.entity.Sync;
import ru.complitex.sync.entity.SyncCatalog;
import ru.complitex.sync.service.IAddressService;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class AddressService implements IAddressService {
    public String getUrl() {
        return "http://localhost:7777/api";
    }

    public String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public String getJson(String catalog, SyncCatalog syncCatalog) {
        try {
            String query = "?catalog=" + catalog;

            if (syncCatalog.getCountry() != null) {
                query += "&country=" +encode(syncCatalog.getCountry());
            }

            if (syncCatalog.getRegion() != null) {
                query += "&region=" + encode(syncCatalog.getRegion());
            }

            if (syncCatalog.getCityType() != null) {
                query += "&cityType=" + encode(syncCatalog.getCityType());
            }

            if (syncCatalog.getCity() != null) {
                query += "&city=" + encode(syncCatalog.getCity());
            }

            if (syncCatalog.getDistrict() != null) {
                query += "&district=" + encode(syncCatalog.getDistrict());
            }

            if (syncCatalog.getStreetType() != null) {
                query += "&streetType=" + encode(syncCatalog.getStreetType());
            }

            if (syncCatalog.getStreet() != null) {
                query += "&street=" + encode(syncCatalog.getStreet());
            }

            if (syncCatalog.getHouseNumber() != null) {
                query += "&houseNumber=" + encode(syncCatalog.getHouseNumber());
            }

            if (syncCatalog.getHousePart() != null) {
                query += "&housePart=" + encode(syncCatalog.getHousePart());
            }

            if (syncCatalog.getDate() != null) {
                query += "&date=" + syncCatalog.getDate().toString();
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(getUrl() + query).openConnection();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateName(Sync sync, JSONObject name) {
        if (name.has("ru")) {
            sync.setName(name.getString("ru"));
        }

        if (name.has("ua")) {
            sync.setAltName(name.getString("ua"));
        }
    }

    private void updateAdditionalName(Sync sync, JSONObject additionalName) {
        if (additionalName.has("ru")) {
            sync.setAdditionalName(additionalName.getString("ru"));
        }

        if (additionalName.has("ua")) {
            sync.setAltAdditionalName(additionalName.getString("ua"));
        }
    }

    private void sync(String catalog, SyncCatalog syncCatalog, BiConsumer<Sync, JSONObject> consumer) {
        syncCatalog.setSyncs(new ArrayList<>());

        String json = getJson(catalog, syncCatalog);

        for (Object object : new JSONArray(json)) {
            JSONObject item = (JSONObject) object;

            Sync sync = new Sync();

            sync.setExternalId(item.getLong("id"));

            consumer.accept(sync, item);

            syncCatalog.getSyncs().add(sync);
        }

        syncCatalog.setCode(1);
    }

    @Override
    public void getOrganizationSyncs(SyncCatalog syncCatalog) {

    }

    @Override
    public void getCountrySyncs(SyncCatalog syncCatalog) {
        sync("country", syncCatalog, (sync, item) -> {
            updateName(sync, item.getJSONObject("countryName"));
        });
    }

    @Override
    public void getRegionSyncs(SyncCatalog syncCatalog) {
        sync("region", syncCatalog, (sync, item) -> {
            sync.setParentId(item.getLong("countryId"));
            updateName(sync, item.getJSONObject("regionName"));
        });
    }

    @Override
    public void getCityTypeSyncs(SyncCatalog syncCatalog) {
        sync("cityType", syncCatalog, (sync, item) -> {
            updateName(sync, item.getJSONObject("cityTypeName"));
            updateAdditionalName(sync, item.getJSONObject("cityTypeShortName"));
        });
    }

    @Override
    public void getCitySyncs(SyncCatalog syncCatalog) {
        sync("city", syncCatalog, (sync, item) -> {
            sync.setParentId(item.getLong("regionId"));
            sync.setAdditionalParentId(item.getLong("cityTypeId") + "");
            updateName(sync, item.getJSONObject("cityName"));
        });
    }

    @Override
    public void getDistrictSyncs(SyncCatalog syncCatalog) {
        sync("district", syncCatalog, (sync, item) -> {
            sync.setParentId(item.getLong("cityId"));
            updateName(sync, item.getJSONObject("districtName"));
        });
    }

    @Override
    public void getStreetTypeSyncs(SyncCatalog syncCatalog) {
        sync("streetType", syncCatalog, (sync, item) -> {
            updateName(sync, item.getJSONObject("streetTypeName"));
            updateAdditionalName(sync, item.getJSONObject("streetTypeShortName"));
        });
    }

    @Override
    public void getStreetSyncs(SyncCatalog syncCatalog) {
        sync("street", syncCatalog, (sync, item) -> {
            sync.setParentId(item.getLong("cityId"));
            sync.setAdditionalParentId(item.getLong("streetTypeId") + "");
            updateName(sync, item.getJSONObject("streetName"));
        });
    }

    @Override
    public void getHouseSyncs(SyncCatalog syncCatalog) {
        sync("house", syncCatalog, (sync, item) -> {
            sync.setParentId(item.getLong("streetId"));
            sync.setAdditionalParentId(item.getLong("districtId") + "");
            updateName(sync, item.getJSONObject("houseNumber"));
            updateAdditionalName(sync, item.getJSONObject("housePart"));
        });
    }

    @Override
    public void getFlatSyncs(SyncCatalog syncCatalog) {
        sync("flat", syncCatalog, (sync, item) -> {
            sync.setParentId(item.getLong("houseId"));
            sync.setAdditionalParentId(item.getLong("streetId") + "");
            updateName(sync, item.getJSONObject("flatNumber"));
        });
    }
}
