package ru.complitex.pspoffice.address.api;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.*;

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
        int catalog = getCatalog(parameters.get("catalogName").toString());

        String countryName = parameters.get("countryName").toOptionalString();
        String regionName = parameters.get("regionName").toOptionalString();
        String cityTypeName = parameters.get("cityTypeName").toOptionalString();
        String cityName = parameters.get("cityName").toOptionalString();
        String districtName = parameters.get("districtName").toOptionalString();
        String streetTypeName = parameters.get("streetTypeName").toOptionalString();
        String streetName = parameters.get("streetName").toOptionalString();

        String dateString = parameters.get("date").toOptionalString();

        LocalDate date = dateString != null ? LocalDate.parse(dateString) : LocalDate.now();

        Long country = null;
        Long region = null;
        Long cityType = null;
        Long city = null;
        Long district = null;
        Long streetType = null;
        Long street = null;

        if (countryName != null) {
            country = catalogService.getItem(Country.CATALOG, date)
                    .withText(Country.COUNTRY_NAME, Locale.SYSTEM, countryName)
                    .getOptional().map(Item::getId).orElse(null);
        }

        if (regionName != null) {
            region = catalogService.getItem(Region.CATALOG, date)
                    .withReferenceId(Region.COUNTRY, country)
                    .withText(Region.REGION_NAME, Locale.SYSTEM, regionName)
                    .getOptional().map(Item::getId).orElse(null);
        }

        if (cityTypeName != null) {
            cityType = catalogService.getItem(CityType.CATALOG, date)
                    .withText(CityType.CITY_TYPE_NAME, Locale.SYSTEM, cityTypeName)
                    .getOptional().map(Item::getId).orElse(null);
        }

        if (cityName != null) {
            city = catalogService.getItem(City.CATALOG, date)
                    .withReferenceId(City.REGION, region)
                    .withReferenceId(City.CITY_TYPE, cityType)
                    .withText(City.CITY_NAME, Locale.SYSTEM, cityName)
                    .getOptional().map(Item::getId).orElse(null);
        }

        if (districtName != null) {
            district = catalogService.getItem(District.CATALOG, date)
                    .withReferenceId(District.CITY, city)
                    .withText(District.DISTRICT_NAME, Locale.SYSTEM, districtName)
                    .getOptional().map(Item::getId).orElse(null);
        }

        if (streetTypeName != null) {
            streetType = catalogService.getItem(StreetType.CATALOG, date)
                    .withText(StreetType.STREET_TYPE_NAME, Locale.SYSTEM, streetTypeName)
                    .getOptional().map(Item::getId).orElse(null);
        }

        if (streetName != null) {
            street = catalogService.getItem(Street.CATALOG, date)
                    .withReferenceId(Street.CITY, city)
                    .withReferenceId(Street.STREET_TYPE, streetType)
                    .withText(Street.STREET_NAME, Locale.SYSTEM, streetName)
                    .getOptional().map(Item::getId).orElse(null);
        }

        JSONArray jsonArray = new JSONArray();

        CatalogService.FilterItemBuilder<List<Item>> builder = catalogService.getItems(catalog, date);

        switch (catalog) {
            case Region.CATALOG -> {
                builder.withReferenceId(Region.COUNTRY, country);
            }
            case City.CATALOG -> {
                builder.withReferenceId(City.REGION, region);
            }
            case District.CATALOG -> {
                builder.withReferenceId(District.CITY, city);
            }
            case Street.CATALOG -> {
                builder.withReferenceId(Street.CITY, city);
            }
            case House.CATALOG -> {
                if (district != null) {
                    builder.withReferenceId(House.DISTRICT, district);
                }

                builder.withReferenceId(House.STREET, street);
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
                json.put("country", item.getReferenceId(Region.COUNTRY));
                json.put("regionName", getName(item, Region.REGION_NAME));
            }

            case CityType.CATALOG -> {
                json.put("cityTypeName", getName(item, CityType.CITY_TYPE_NAME));
                json.put("cityTypeShortName", getName(item, CityType.CITY_TYPE_SHORT_NAME));
            }

            case City.CATALOG -> {
                json.put("region", item.getReferenceId(City.REGION));
                json.put("cityType", item.getReferenceId(City.CITY_TYPE));
                json.put("cityName", getName(item, City.CITY_NAME));
            }

            case District.CATALOG -> {
                json.put("city", item.getReferenceId(District.CITY));
                json.put("districtName", getName(item, District.DISTRICT_NAME));
            }

            case StreetType.CATALOG -> {
                json.put("streetTypeName", getName(item, StreetType.STREET_TYPE_NAME));
                json.put("streetTypeShortName", getName(item, StreetType.STREET_TYPE_SHORT_NAME));
            }

            case Street.CATALOG -> {
                json.put("city", item.getReferenceId(Street.CITY));
                json.put("streetType", item.getReferenceId(Street.STREET_TYPE));
                json.put("streetName", getName(item, Street.STREET_NAME));
            }

            case House.CATALOG -> {
                json.put("district", item.getReferenceId(House.DISTRICT));
                json.put("street", item.getReferenceId(House.STREET));
                json.put("houseNumber", getName(item, House.HOUSE_NUMBER));
                json.put("housePart", getName(item, House.HOUSE_PART));
            }

            case Flat.CATALOG -> {
                json.put("street", item.getReferenceId(Flat.STREET));
                json.put("house", item.getReferenceId(Flat.HOUSE));
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
