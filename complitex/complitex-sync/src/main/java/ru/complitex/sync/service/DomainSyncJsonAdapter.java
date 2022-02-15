package ru.complitex.sync.service;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.DictionaryConfig;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.entity.DomainSyncParameter;
import ru.complitex.sync.entity.SyncEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author Ivanov Anatoliy
 */
@Stateless
public class DomainSyncJsonAdapter {
    @EJB
    private ConfigBean configBean;

    public String getUrl() {
        return configBean.getString(DictionaryConfig.SYNC_URL);
    }

    public String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "utf-8");
    }

    public String getJson(DomainSyncParameter parameter) throws RemoteCallException {
        try {
            String query = "?catalogName=" + parameter.getCatalogName();

            if (parameter.getCountryName() != null) {
                query += "&countryName=" +encode(parameter.getCountryName());
            }

            if (parameter.getRegionName() != null) {
                query += "&regionName=" + encode(parameter.getRegionName());
            }

            if (parameter.getCityTypeName() != null) {
                query += "&cityTypeName=" + encode(parameter.getCityTypeName());
            }

            if (parameter.getCityName() != null) {
                query += "&cityName=" + encode(parameter.getCityName());
            }

            if (parameter.getDistrictName() != null) {
                query += "&districtName=" + encode(parameter.getDistrictName());
            }

            if (parameter.getStreetTypeName() != null) {
                query += "&streetTypeName=" + encode(parameter.getStreetTypeName());
            }

            if (parameter.getStreetName() != null) {
                query += "&streetName=" + encode(parameter.getStreetName());
            }

            if (parameter.getDate() != null) {
                LocalDate localDate = parameter.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                query += "&date=" + localDate.toString();
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(getUrl() + query).openConnection();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }
    }

    private void updateName(DomainSync sync, JSONObject name) {
        if (name.has("ru")) {
            sync.setName(name.getString("ru"));
        }

        if (name.has("ua")) {
            sync.setAltName(name.getString("ua"));
        }
    }

    private void updateAdditionalName(DomainSync sync, JSONObject additionalName) {
        if (additionalName.has("ru")) {
            sync.setAdditionalName(additionalName.getString("ru"));
        }

        if (additionalName.has("ua")) {
            sync.setAltAdditionalName(additionalName.getString("ua"));
        }
    }

    private Cursor<DomainSync> getSyncs(SyncEntity entity, DomainSyncParameter parameter,
                                        BiConsumer<DomainSync, JSONObject> consumer) throws RemoteCallException {
        List<DomainSync> list = new ArrayList<>();

        String json = getJson(parameter);

        for (Object object : new JSONArray(json)) {
            JSONObject item = (JSONObject) object;

            DomainSync sync = new DomainSync(entity, item.getLong("id"));

            consumer.accept(sync, item);

            list.add(sync);
        }

        return new Cursor<>(list);
    }

    public Cursor<DomainSync> getCountrySyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.COUNTRY, parameter.catalog("country"), (sync, item) -> {
            updateName(sync, item.getJSONObject("countryName"));
        });
    }

    public Cursor<DomainSync> getRegionSyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.REGION, parameter.catalog("region"), (sync, item) -> {
            sync.setParentId(item.getLong("country"));
            updateName(sync, item.getJSONObject("regionName"));
        });
    }

    public Cursor<DomainSync> getCityTypeSyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.CITY_TYPE, parameter.catalog("cityType"), (sync, item) -> {
            updateName(sync, item.getJSONObject("cityTypeName"));
            updateAdditionalName(sync, item.getJSONObject("cityTypeShortName"));
        });
    }

    public Cursor<DomainSync> getCitySyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.CITY, parameter.catalog("city"), (sync, item) -> {
            sync.setParentId(item.getLong("region"));
            sync.setAdditionalParentId(item.getLong("cityType") + "");
            updateName(sync, item.getJSONObject("cityName"));
        });
    }

    public Cursor<DomainSync> getDistrictSyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.DISTRICT, parameter.catalog("district"), (sync, item) -> {
            sync.setParentId(item.getLong("city"));
            updateName(sync, item.getJSONObject("districtName"));
        });
    }

    public Cursor<DomainSync> getStreetTypeSyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.STREET_TYPE, parameter.catalog("streetType"), (sync, item) -> {
            updateName(sync, item.getJSONObject("streetTypeName"));
            updateAdditionalName(sync, item.getJSONObject("streetTypeShortName"));
        });
    }

    public Cursor<DomainSync> getStreetSyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.STREET, parameter.catalog("street"), (sync, item) -> {
            sync.setParentId(item.getLong("city"));
            sync.setAdditionalParentId(item.getLong("streetType") + "");
            updateName(sync, item.getJSONObject("streetName"));
        });
    }

    public Cursor<DomainSync> getBuildingSyncs(DomainSyncParameter parameter) throws RemoteCallException {
        return getSyncs(SyncEntity.BUILDING, parameter.catalog("house"), (sync, item) -> {
            sync.setParentId(item.getLong("street"));
            sync.setAdditionalParentId(item.getLong("district") + "");
            updateName(sync, item.getJSONObject("houseNumber"));
            updateAdditionalName(sync, item.getJSONObject("housePart"));
        });
    }
}
