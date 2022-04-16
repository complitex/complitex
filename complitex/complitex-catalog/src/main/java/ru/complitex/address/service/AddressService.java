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
        if (regionId == null) {
            return "";
        }

        String regionName = catalogService.getText(Region.CATALOG, regionId, Region.REGION_NAME, Locale.SYSTEM, date);

        Long countryId = catalogService.getReferenceId(Region.CATALOG, regionId, Region.COUNTRY, date);

        String countryName = catalogService.getText(Country.CATALOG, countryId, Country.COUNTRY_NAME, Locale.SYSTEM, date);

        return countryName + ", " + regionName;
    }

    public String getFullCityName(Long cityId, LocalDate date) {
        if (cityId == null) {
            return "";
        }

        Long cityTypeId = catalogService.getReferenceId(City.CATALOG, cityId, City.CITY_TYPE, date);

        String cityTypeName = catalogService.getText(CityType.CATALOG, cityTypeId, CityType.CITY_TYPE_SHORT_NAME, Locale.SYSTEM, date);

        String cityName = catalogService.getText(City.CATALOG, cityId, City.CITY_NAME, Locale.SYSTEM, date);

        Long regionId = catalogService.getReferenceId(City.CATALOG, cityId, City.REGION, date);

        return getFullRegionName(regionId, date) + ", " + cityTypeName + ". " + cityName;
    }

    public String getFullDistrictName(Long districtId, LocalDate date) {
        if (districtId == null) {
            return "";
        }

        String districtName = catalogService.getText(District.CATALOG, districtId, District.DISTRICT_NAME, Locale.SYSTEM, date);

        Long cityId = catalogService.getReferenceId(District.CATALOG, districtId, District.CITY, date);

        return getFullCityName(cityId, date) + ", " + districtName;
    }

    public String getFullStreetName(Long streetId, LocalDate date) {
        if (streetId == null) {
            return "";
        }

        Long streetTypeId = catalogService.getReferenceId(Street.CATALOG, streetId, Street.STREET_TYPE, date);

        String streetTypeName = catalogService.getText(StreetType.CATALOG, streetTypeId, StreetType.STREET_TYPE_SHORT_NAME, Locale.SYSTEM, date);

        String streetName = catalogService.getText(Street.CATALOG, streetId, Street.STREET_NAME, Locale.SYSTEM, date);

        Long cityId = catalogService.getReferenceId(Street.CATALOG, streetId, Street.CITY, date);

        return getFullCityName(cityId, date) + ", " + streetTypeName + ". " + streetName;
    }

    public String getFullHouseName(Long houseId, LocalDate date) {
        if (houseId == null) {
            return "";
        }

        String houseNumber = catalogService.getText(House.CATALOG, houseId, House.HOUSE_NUMBER, Locale.SYSTEM, date);

        String housePart = catalogService.getText(House.CATALOG, houseId, House.HOUSE_PART, Locale.SYSTEM, date);

        Long streetId = catalogService.getReferenceId(House.CATALOG, houseId, House.STREET, date);

        return getFullStreetName(streetId, date) + ", Д. " + houseNumber + (housePart != null ? ", КОРП. " + housePart : "");
    }

    public String getFullFlatName(Long flatId, LocalDate date) {
        if (flatId == null) {
            return "";
        }

        String flatName = catalogService.getText(Flat.CATALOG, flatId, Flat.FLAT_NUMBER, Locale.SYSTEM, date);

        Long houseId = catalogService.getReferenceId(Flat.CATALOG, flatId, Flat.HOUSE, date);

        return getFullHouseName(houseId, date) + ", КВ. " + flatName;
    }
}
