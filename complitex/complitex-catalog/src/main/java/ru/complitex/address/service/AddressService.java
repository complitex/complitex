package ru.complitex.address.service;

import ru.complitex.address.entity.*;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.service.CatalogService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
@RequestScoped
public class AddressService {
    @Inject
    private CatalogService catalogService;

    public String getFullRegionName(Long regionId, LocalDate date) {
        String regionName = catalogService.getText(Region.CATALOG, regionId, Region.REGION_NAME, Locale.SYSTEM, date);

        Long countryId = catalogService.getReferenceId(Region.CATALOG, regionId, Region.COUNTRY, date);

        String countryName = catalogService.getText(Country.CATALOG, countryId, Country.COUNTRY_NAME, Locale.SYSTEM, date);

        return countryName + ", " + regionName;
    }

    public String getFullCityName(Long cityId, LocalDate date) {
        Long cityTypeId = catalogService.getReferenceId(City.CATALOG, cityId, City.CITY_TYPE, date);

        String cityTypeName = catalogService.getText(CityType.CATALOG, cityTypeId, CityType.CITY_TYPE_SHORT_NAME, Locale.SYSTEM, date);

        String cityName = catalogService.getText(City.CATALOG, cityId, City.CITY_NAME, Locale.SYSTEM, date);

        Long regionId = catalogService.getReferenceId(City.CATALOG, cityId, City.REGION, date);

        return getFullRegionName(regionId, date) + ", " + cityTypeName + ". " + cityName;
    }

    public String getFullStreetName(Long streetId, LocalDate date) {
        Long streetTypeId = catalogService.getReferenceId(Street.CATALOG, streetId, Street.STREET_TYPE, date);

        String streetTypeName = catalogService.getText(StreetType.CATALOG, streetTypeId, StreetType.STREET_TYPE_SHORT_NAME, Locale.SYSTEM, date);

        String streetName = catalogService.getText(Street.CATALOG, streetId, Street.STREET_NAME, Locale.SYSTEM, date);

        Long cityId = catalogService.getReferenceId(Street.CATALOG, streetId, Street.CITY, date);

        return getFullCityName(cityId, date) + ", " + streetTypeName + ". " + streetName;
    }
}
