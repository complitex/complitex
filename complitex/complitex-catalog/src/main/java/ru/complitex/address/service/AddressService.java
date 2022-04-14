package ru.complitex.address.service;

import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
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
}
