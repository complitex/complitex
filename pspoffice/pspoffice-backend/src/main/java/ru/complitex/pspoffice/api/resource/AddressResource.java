package ru.complitex.pspoffice.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.country.CountryStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.region.RegionStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.util.Locales;
import ru.complitex.pspoffice.api.model.AddressName;
import ru.complitex.pspoffice.api.model.AddressObject;
import ru.complitex.pspoffice.api.model.BuildingObject;

import javax.ejb.EJB;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author Anatoly A. Ivanov
 *         27.04.2017 19:07
 */
@Path("address")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Address Database API")
public class AddressResource {
    @EJB
    private CountryStrategy countryStrategy;

    @EJB
    private RegionStrategy regionStrategy;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("ping").build();
    }

    @GET
    @Path("country/{id}")
    @ApiOperation(value = "Get country by id", response = AddressObject.class)
    public Response getCountry(@PathParam("id") Long id){
        DomainObject d = countryStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressObject(d.getObjectId(), getAddressNames(d, CountryStrategy.NAME))).build();
    }

    private List<AddressName> getAddressNames(DomainObject domainObject, Long attributeTypeId){
        if (domainObject.getAttribute(attributeTypeId) == null ||
                domainObject.getAttribute(attributeTypeId).getStringCultures().isEmpty()){
            return null;
        }

        return domainObject.getAttribute(attributeTypeId).getStringCultures().stream()
                .filter(s -> s.getValue() != null)
                .map(s -> new AddressName(Locales.getLanguage(s.getLocaleId()), s.getValue()))
                .collect(Collectors.toList());
    }

    @GET
    @Path("country")
    @ApiOperation(value = "Get county list by query", response = AddressObject.class, responseContainer = "List")
    public Response getCountries(@QueryParam("query") @NotNull String query,
                                 @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(CountryStrategy.NAME, query);
        filter.setCount(limit);

        return Response.ok(countryStrategy.getList(filter).stream()
                .map(d -> new AddressObject(d.getObjectId(), getAddressNames(d, CountryStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("region/{id}")
    @ApiOperation(value = "Get region by id", response = AddressObject.class)
    public Response getRegion(@PathParam("id") Long id){
        DomainObject d = regionStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressObject(d.getObjectId(), getAddressNames(d, RegionStrategy.NAME))).build();
    }

    @GET
    @Path("region")
    @ApiOperation(value = "Get region list by query", response = AddressObject.class, responseContainer = "List")
    public Response getRegion(@QueryParam("query") @NotNull String query,
                              @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(RegionStrategy.NAME, query);
        filter.setCount(limit);

        return Response.ok(regionStrategy.getList(filter).stream()
                .map(d -> new AddressObject(d.getObjectId(), getAddressNames(d, RegionStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("city-type/{id}")
    @ApiOperation(value = "Get city type by id", response = AddressObject.class)
    public Response getCityType(@PathParam("id") Long id){
        DomainObject d = cityTypeStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressObject(d.getObjectId(),
                getAddressNames(d, CityTypeStrategy.NAME),
                getAddressNames(d, CityTypeStrategy.SHORT_NAME))).build();
    }

    @GET
    @Path("city-type")
    @ApiOperation(value = "Get city types by query", response = AddressObject.class, responseContainer = "List")
    public Response getCityTypes(@QueryParam("query") @NotNull String query,
                                 @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(CityTypeStrategy.NAME, query);
        filter.setCount(limit);

        return Response.ok(cityTypeStrategy.getList(filter).stream()
                .map(d -> new AddressObject(d.getObjectId(),
                        getAddressNames(d, CityTypeStrategy.NAME),
                        getAddressNames(d, CityTypeStrategy.SHORT_NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("city/{id}")
    @ApiOperation(value = "Get city by id", response = AddressObject.class)
    public Response getCity(@PathParam("id") Long id){
        DomainObject d = cityStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressObject(d.getObjectId(), d.getParentId(),
                d.getAttribute(CityStrategy.CITY_TYPE).getValueId(),
                getAddressNames(d, CityStrategy.NAME))).build();
    }

    @GET
    @Path("city")
    @ApiOperation(value = "Get city list by query", response = AddressObject.class, responseContainer = "List")
    public Response getCities(@QueryParam("query") @NotNull String query,
                              @QueryParam("parentId") Long parentId,
                              @QueryParam("typeId") Long typeId,
                              @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("region", parentId);
        filter.addAttribute(CityStrategy.CITY_TYPE, typeId);
        filter.addAttribute(CityStrategy.NAME, query);
        filter.setCount(limit);

        return Response.ok(cityStrategy.getList(filter).stream()
                .map(d -> new AddressObject(d.getObjectId(), d.getParentId(),
                        d.getAttribute(CityStrategy.CITY_TYPE).getValueId(),
                        getAddressNames(d, CityStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("district/{id}")
    @ApiOperation(value = "Get district by id", response = AddressObject.class)
    public Response getDistrict(@PathParam("id") Long id){
        DomainObject d = districtStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressObject(d.getObjectId(), d.getParentId(),
                d.getStringValue(DistrictStrategy.CODE),
                getAddressNames(d, DistrictStrategy.NAME))).build();
    }

    @GET
    @Path("district")
    @ApiOperation(value = "Get district list by query", response = AddressObject.class, responseContainer = "List")
    public Response getDistricts(@QueryParam("query") @NotNull String query,
                                 @QueryParam("parentId") Long parentId,
                                 @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("city", parentId);
        filter.addAttribute(DistrictStrategy.NAME, query);
        filter.setCount(limit);

        return Response.ok(districtStrategy.getList(filter).stream()
                .map(d -> new AddressObject(d.getObjectId(), d.getParentId(),
                        d.getStringValue(DistrictStrategy.CODE),
                        getAddressNames(d, DistrictStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("street/{id}")
    @ApiOperation(value = "Get street by id", response = AddressObject.class)
    public Response getStreet(@PathParam("id") Long id){
        DomainObject d = streetStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressObject(d.getObjectId(), d.getParentId(), d.getValue(StreetStrategy.STREET_TYPE),
                d.getStringValue(StreetStrategy.STREET_CODE), getAddressNames(d, StreetStrategy.NAME))).build();
    }

    @GET
    @Path("street")
    @ApiOperation(value = "Get street list by query")
    public Response getStreets(@QueryParam("query") @NotNull String query,
                               @QueryParam("parentId") Long parentId,
                               @QueryParam("limit") @DefaultValue("10") @Max(1000) Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("city", parentId);
        filter.addAttribute(StreetStrategy.NAME, query);
        filter.setCount(limit);

        return Response.ok(streetStrategy.getList(filter).stream()
                .map(d -> new AddressObject(d.getObjectId(), d.getParentId(), d.getValue(StreetStrategy.STREET_TYPE),
                        d.getStringValue(StreetStrategy.STREET_CODE), getAddressNames(d, StreetStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    private BuildingObject getBuildingObject(Building building){
        DomainObject a = building.getAccompaniedAddress();

        BuildingObject object = new BuildingObject(building.getObjectId(), a.getParentId(),
                getAddressNames(a, BuildingAddressStrategy.NUMBER), getAddressNames(a, BuildingAddressStrategy.CORP),
                getAddressNames(a, BuildingAddressStrategy.STRUCTURE));

        if (!building.getAlternativeAddresses().isEmpty()){
            List<BuildingObject> alternatives = new ArrayList<>();
            object.setAlternatives(alternatives);

            building.getAlternativeAddresses().forEach(alt -> {
                alternatives.add(new BuildingObject(null, alt.getParentId(),
                        getAddressNames(alt, BuildingAddressStrategy.NUMBER), getAddressNames(alt, BuildingAddressStrategy.CORP),
                        getAddressNames(alt, BuildingAddressStrategy.STRUCTURE)));
            });
        }

        return object;
    }

    @GET
    @Path("building/{id}")
    @ApiOperation(value = "Get building by id", response = BuildingObject.class)
    public Response getBuilding(@PathParam("id") Long id){
        Building building = buildingStrategy.getDomainObject(id, true);

        if (building == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(getBuildingObject(building)).build();
    }

    @GET
    @Path("building")
    @ApiOperation(value = "Get building list by query")
    public Response getBuildings(@QueryParam("query") String query,
                                 @QueryParam("parentId") @NotNull Long parentId,
                                 @QueryParam("limit") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAdditionalParam(BuildingStrategy.P_NUMBER, query);
        filter.addAdditionalParam(BuildingStrategy.P_STREET, parentId);

        return Response.ok(buildingStrategy.getList(filter).stream()
                .map(this::getBuildingObject).collect(Collectors.toList())).build();
    }
}
