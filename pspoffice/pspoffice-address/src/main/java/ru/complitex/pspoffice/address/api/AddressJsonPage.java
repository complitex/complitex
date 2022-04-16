package ru.complitex.pspoffice.address.api;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.*;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.service.CatalogService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class AddressJsonPage extends WebPage {
    @Inject
    private CatalogService catalogService;

    public AddressJsonPage(PageParameters parameters) {
        int catalog = getCatalog(parameters.get("catalog").toString());

        String country = parameters.get("country").toOptionalString();
        String region = parameters.get("region").toOptionalString();
        String cityType = parameters.get("cityType").toOptionalString();
        String city = parameters.get("city").toOptionalString();
        String district = parameters.get("district").toOptionalString();
        String streetType = parameters.get("streetType").toOptionalString();
        String street = parameters.get("street").toOptionalString();
        String houseNumber = parameters.get("houseNumber").toOptionalString();
        String housePart = parameters.get("housePart").toOptionalString();

        String dateString = parameters.get("date").toOptionalString();

        LocalDate date = dateString != null ? LocalDate.parse(dateString) : LocalDate.now();

        Long countryId = null;
        Long regionId = null;
        Long cityTypeId = null;
        Long cityId = null;
        Long districtId = null;
        Long streetTypeId = null;
        Long streetId = null;
        Long houseId = null;

        if (country != null) {
            countryId = catalogService.getItem(Country.CATALOG, date)
                    .withText(Country.COUNTRY_NAME, country, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(-1L);
        }

        if (region != null) {
            regionId = catalogService.getItem(Region.CATALOG, date)
                    .withReferenceId(Region.COUNTRY, countryId)
                    .withText(Region.REGION_NAME, region, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(-1L);
        }

        if (cityType != null) {
            cityTypeId = catalogService.getItem(CityType.CATALOG, date)
                    .withText(CityType.CITY_TYPE_NAME, cityType, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(null);

            if (cityTypeId == null) {
                cityTypeId = catalogService.getItem(CityType.CATALOG, date)
                        .withText(CityType.CITY_TYPE_SHORT_NAME, cityType, Locale.SYSTEM)
                        .getOptional().map(Item::getId).orElse(-1L);
            }
        }

        if (city != null) {
            cityId = catalogService.getItem(City.CATALOG, date)
                    .withReferenceId(City.REGION, regionId)
                    .withReferenceId(City.CITY_TYPE, cityTypeId)
                    .withText(City.CITY_NAME, city, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(-1L);
        }

        if (district != null) {
            districtId = catalogService.getItem(District.CATALOG, date)
                    .withReferenceId(District.CITY, cityId)
                    .withText(District.DISTRICT_NAME, district, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(-1L);
        }

        if (streetType != null) {
            streetTypeId = catalogService.getItem(StreetType.CATALOG, date)
                    .withText(StreetType.STREET_TYPE_NAME, streetType, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(null);

            if (streetTypeId == null) {
                streetTypeId = catalogService.getItem(StreetType.CATALOG, date)
                        .withText(StreetType.STREET_TYPE_SHORT_NAME, streetType, Locale.SYSTEM)
                        .getOptional().map(Item::getId).orElse(-1L);
            }
        }

        if (street != null) {
            streetId = catalogService.getItem(Street.CATALOG, date)
                    .withReferenceId(Street.CITY, cityId)
                    .withReferenceId(Street.STREET_TYPE, streetTypeId)
                    .withText(Street.STREET_NAME, street, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(-1L);
        }

        if (houseNumber != null) {
            houseId =  catalogService.getItem(House.CATALOG, date)
                    .withReferenceId(House.DISTRICT, districtId)
                    .withReferenceId(House.STREET, streetId)
                    .withText(House.HOUSE_NUMBER, houseNumber, Locale.SYSTEM)
                    .withText(House.HOUSE_PART, housePart != null && !housePart.isEmpty() ? housePart : null, Locale.SYSTEM)
                    .getOptional().map(Item::getId).orElse(-1L);
        }

        JSONArray jsonArray = new JSONArray();

        CatalogService.FilterItemBuilder<List<Item>> builder = catalogService.getItems(catalog, date);

        switch (catalog) {
            case Region.CATALOG -> {
                builder.withReferenceId(Region.COUNTRY, countryId);
            }
            case City.CATALOG -> {
                builder.withReferenceId(City.REGION, regionId);
            }
            case District.CATALOG -> {
                builder.withReferenceId(District.CITY, cityId);
            }
            case Street.CATALOG -> {
                builder.withReferenceId(Street.CITY, cityId);
            }
            case House.CATALOG -> {
                if (districtId != null) {
                    builder.withReferenceId(House.DISTRICT, districtId);
                }

                builder.withReferenceId(House.STREET, streetId);
            }
            case Flat.CATALOG -> {
                builder.withReferenceId(Flat.HOUSE, houseId);
            }
        }

        builder.get().forEach(item -> jsonArray.put(getJson(item)));

        getRequestCycle().scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "utf-8", jsonArray.toString()));
    }

    private int getCatalog(String catalogName) {
        return switch (catalogName) {
            case "country" -> Country.CATALOG;
            case "region" -> Region.CATALOG;
            case "cityType" -> CityType.CATALOG;
            case "city" -> City.CATALOG;
            case "district" -> District.CATALOG;
            case "streetType" -> StreetType.CATALOG;
            case "street" -> Street.CATALOG;
            case "house" -> House.CATALOG;
            case "flat" -> Flat.CATALOG;
            default -> throw new RuntimeException(catalogName);
        };
    }

    private JSONObject getName(Item item, int value) {
        JSONObject name = new JSONObject();

        String nameRu =  item.getText(value, Locale.RU);

        if (nameRu != null) {
            name.put("ru", nameRu);
        }

        String nameUa = item.getText(value, Locale.UA);

        if (nameUa != null) {
            name.put("ua", nameUa);
        }

        return name;
    }

    private JSONObject getJson(Item item) {
        JSONObject json = new JSONObject();

        json.put("id", item.getId());

        switch (item.getCatalog().getKeyId()) {
            case Country.CATALOG -> {
                json.put("countryName", getName(item, Country.COUNTRY_NAME));
            }

            case Region.CATALOG -> {
                json.put("countryId", item.getReferenceId(Region.COUNTRY));
                json.put("regionName", getName(item, Region.REGION_NAME));
            }

            case CityType.CATALOG -> {
                json.put("cityTypeName", getName(item, CityType.CITY_TYPE_NAME));
                json.put("cityTypeShortName", getName(item, CityType.CITY_TYPE_SHORT_NAME));
            }

            case City.CATALOG -> {
                json.put("regionId", item.getReferenceId(City.REGION));
                json.put("cityTypeId", item.getReferenceId(City.CITY_TYPE));
                json.put("cityName", getName(item, City.CITY_NAME));
            }

            case District.CATALOG -> {
                json.put("cityId", item.getReferenceId(District.CITY));
                json.put("districtName", getName(item, District.DISTRICT_NAME));
            }

            case StreetType.CATALOG -> {
                json.put("streetTypeName", getName(item, StreetType.STREET_TYPE_NAME));
                json.put("streetTypeShortName", getName(item, StreetType.STREET_TYPE_SHORT_NAME));
            }

            case Street.CATALOG -> {
                json.put("cityId", item.getReferenceId(Street.CITY));
                json.put("streetTypeId", item.getReferenceId(Street.STREET_TYPE));
                json.put("streetName", getName(item, Street.STREET_NAME));
            }

            case House.CATALOG -> {
                json.put("districtId", item.getReferenceId(House.DISTRICT));
                json.put("streetId", item.getReferenceId(House.STREET));
                json.put("houseNumber", getName(item, House.HOUSE_NUMBER));
                json.put("housePart", getName(item, House.HOUSE_PART));
            }

            case Flat.CATALOG -> {
                json.put("houseId", item.getReferenceId(Flat.HOUSE));
                json.put("flatNumber", getName(item, Flat.FLAT_NUMBER));
            }
        }

        return json;
    }

    @Override
    public MarkupType getMarkupType() {
        return new MarkupType("html", "application/json");
    }
}
