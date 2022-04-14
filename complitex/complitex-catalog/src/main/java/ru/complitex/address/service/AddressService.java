package ru.complitex.address.service;

import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
import ru.complitex.catalog.entity.Item;
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

    public String getFullRegionNameByCity(Item item, LocalDate date) {
        String regionName = catalogService.getText(Region.CATALOG, item.getReferenceId(City.REGION), Region.REGION_NAME, Locale.SYSTEM, date);

        Long countryId = catalogService.getReferenceId(Region.CATALOG, item.getReferenceId(City.REGION), Region.COUNTRY, date);

        String countryName = catalogService.getText(Country.CATALOG, countryId, Country.COUNTRY_NAME, Locale.SYSTEM, date);

        return countryName + ", " + regionName;
    }
}
