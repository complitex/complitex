package ru.complitex.pspoffice.backend.resource;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.pspoffice.api.model.AddressModel;
import ru.complitex.pspoffice.api.model.BuildingModel;

import javax.ejb.EJB;
import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.complitex.common.util.Locales.RU;
import static org.complitex.common.util.Locales.UA;

/**
 * @author Anatoly A. Ivanov
 *         27.04.2017 19:07
 */
@Path("address")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Address Database API")
public class AddressResource {
    private Logger log = LoggerFactory.getLogger(AddressResource.class);

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
        return Response.ok("pong").build();
    }

    @GET
    @Path("country/{id}")
    @ApiOperation(value = "Get country by id", response = AddressModel.class)
    public Response getCountry(@PathParam("id") Long id){
        DomainObject d = countryStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressModel(d.getObjectId(), d.getStringMap(CountryStrategy.NAME))).build();
    }

    @GET
    @Path("country")
    @ApiOperation(value = "Get country list by query", response = AddressModel.class, responseContainer = "List")
    public Response getCountries(@QueryParam("query") String query,
                                 @QueryParam("offset") @DefaultValue("0") Integer offset,
                                 @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(CountryStrategy.NAME, query);
        filter.setFirst(offset);
        filter.setCount(limit);

        return Response.ok(countryStrategy.getList(filter).stream()
                .map(d -> new AddressModel(d.getObjectId(), d.getStringMap(CountryStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("country/size")
    @ApiOperation(value = "Get country count by query", response = Long.class)
    public Response getCountriesCount(@QueryParam("query") String query){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(CountryStrategy.NAME, query);

        return Response.ok(countryStrategy.getCount(filter)).build();
    }

    @PUT
    @Path("country")
    @ApiOperation(value = "Put country")
    public Response putCountry(AddressModel addressModel){
        try {
            Long id = addressModel.getId();

            if (id != null){
                DomainObject country = countryStrategy.getDomainObject(id);

                if (country != null){
                    country.setStringValue(CountryStrategy.NAME, addressModel.getName().get(RU.getLanguage()), RU);
                    country.setStringValue(CountryStrategy.NAME, addressModel.getName().get(UA.getLanguage()), UA);

                    countryStrategy.update(country);

                    return Response.ok().build();
                }else{
                    return Response.status(NOT_FOUND).build();
                }
            }else{
                DomainObject country = countryStrategy.newInstance();

                country.setStringValue(CountryStrategy.NAME, addressModel.getName().get(RU.getLanguage()), RU);
                country.setStringValue(CountryStrategy.NAME, addressModel.getName().get(UA.getLanguage()), UA);

                countryStrategy.insert(country);

                return Response.status(CREATED).build();
            }
        } catch (Exception e) {
            log.error("put country error", e);

            return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("region/{id}")
    @ApiOperation(value = "Get region by id", response = AddressModel.class)
    public Response getRegion(@PathParam("id") Long id){
        DomainObject d = regionStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressModel(d.getObjectId(), d.getStringMap(RegionStrategy.NAME))).build();
    }

    @GET
    @Path("region")
    @ApiOperation(value = "Get region list by query", response = AddressModel.class, responseContainer = "List")
    public Response getRegions(@QueryParam("query") String query,
                               @QueryParam("offset") @DefaultValue("0") Integer offset,
                              @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(RegionStrategy.NAME, query);
        filter.setFirst(offset);
        filter.setCount(limit);

        return Response.ok(regionStrategy.getList(filter).stream()
                .map(d -> new AddressModel(d.getObjectId(), d.getStringMap(RegionStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("region/size")
    @ApiOperation(value = "Get region count by query", response = Long.class)
    public Response getRegionsCount(@QueryParam("query") String query){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(RegionStrategy.NAME, query);

        return Response.ok(regionStrategy.getCount(filter)).build();
    }

    @GET
    @Path("city-type/{id}")
    @ApiOperation(value = "Get city type by id", response = AddressModel.class)
    public Response getCityType(@PathParam("id") Long id){
        DomainObject d = cityTypeStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressModel(d.getObjectId(),
                d.getStringMap(CityTypeStrategy.NAME),
                d.getStringMap(CityTypeStrategy.SHORT_NAME))).build();
    }

    @GET
    @Path("city-type")
    @ApiOperation(value = "Get city types list by query", response = AddressModel.class, responseContainer = "List")
    public Response getCityTypes(@QueryParam("query") String query,
                                 @QueryParam("offset") @DefaultValue("0") Integer offset,
                                 @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(CityTypeStrategy.NAME, query);
        filter.setFirst(offset);
        filter.setCount(limit);

        return Response.ok(cityTypeStrategy.getList(filter).stream()
                .map(d -> new AddressModel(d.getObjectId(),
                        d.getStringMap(CityTypeStrategy.NAME),
                        d.getStringMap(CityTypeStrategy.SHORT_NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("city-type/size")
    @ApiOperation(value = "Get city types count by query", response = Long.class)
    public Response getCityTypesCount(@QueryParam("query") String query){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAttribute(CityTypeStrategy.NAME, query);

        return Response.ok(cityTypeStrategy.getCount(filter)).build();
    }

    @GET
    @Path("city/{id}")
    @ApiOperation(value = "Get city by id", response = AddressModel.class)
    public Response getCity(@PathParam("id") Long id){
        DomainObject d = cityStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressModel(d.getObjectId(), d.getParentId(),
                d.getAttribute(CityStrategy.CITY_TYPE).getValueId(),
                d.getStringMap(CityStrategy.NAME))).build();
    }

    @GET
    @Path("city")
    @ApiOperation(value = "Get city list by query", response = AddressModel.class, responseContainer = "List")
    public Response getCities(@QueryParam("query") String query,
                              @QueryParam("parentId") Long parentId,
                              @QueryParam("typeId") Long typeId,
                              @QueryParam("offset") @DefaultValue("0") Integer offset,
                              @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("region", parentId);
        filter.addAttribute(CityStrategy.CITY_TYPE, typeId);
        filter.addAttribute(CityStrategy.NAME, query);
        filter.setFirst(offset);
        filter.setCount(limit);

        return Response.ok(cityStrategy.getList(filter).stream()
                .map(d -> new AddressModel(d.getObjectId(), d.getParentId(),
                        d.getAttribute(CityStrategy.CITY_TYPE).getValueId(),
                        d.getStringMap(CityStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("city/size")
    @ApiOperation(value = "Get cities count by query", response = Long.class)
    public Response getCitiesCount(@QueryParam("query") String query,
                              @QueryParam("parentId") Long parentId,
                              @QueryParam("typeId") Long typeId){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("region", parentId);
        filter.addAttribute(CityStrategy.CITY_TYPE, typeId);
        filter.addAttribute(CityStrategy.NAME, query);

        return Response.ok(cityStrategy.getCount(filter)).build();
    }

    @GET
    @Path("district/{id}")
    @ApiOperation(value = "Get district by id", response = AddressModel.class)
    public Response getDistrict(@PathParam("id") Long id){
        DomainObject d = districtStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressModel(d.getObjectId(), d.getParentId(),
                d.getStringValue(DistrictStrategy.CODE),
                d.getStringMap(DistrictStrategy.NAME))).build();
    }

    @GET
    @Path("district")
    @ApiOperation(value = "Get district list by query", response = AddressModel.class, responseContainer = "List")
    public Response getDistricts(@QueryParam("query") String query,
                                 @QueryParam("parentId") Long parentId,
                                 @QueryParam("offset") @DefaultValue("0") Integer offset,
                                 @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("city", parentId);
        filter.addAttribute(DistrictStrategy.NAME, query);
        filter.setFirst(offset);
        filter.setCount(limit);

        return Response.ok(districtStrategy.getList(filter).stream()
                .map(d -> new AddressModel(d.getObjectId(), d.getParentId(),
                        d.getStringValue(DistrictStrategy.CODE),
                        d.getStringMap(DistrictStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("district/size")
    @ApiOperation(value = "Get districts count by query", response = Long.class)
    public Response getDistrictsCount(@QueryParam("query") String query,
                                 @QueryParam("parentId") Long parentId){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("city", parentId);
        filter.addAttribute(DistrictStrategy.NAME, query);

        return Response.ok(districtStrategy.getCount(filter)).build();
    }

    @GET
    @Path("street/{id}")
    @ApiOperation(value = "Get street by id", response = AddressModel.class)
    public Response getStreet(@PathParam("id") Long id){
        DomainObject d = streetStrategy.getDomainObject(id);

        if (d == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(new AddressModel(d.getObjectId(), d.getParentId(), d.getValueId(StreetStrategy.STREET_TYPE),
                d.getStringValue(StreetStrategy.STREET_CODE), d.getStringMap(StreetStrategy.NAME))).build();
    }

    @GET
    @Path("street")
    @ApiOperation(value = "Get street list by query")
    public Response getStreets(@QueryParam("query") String query,
                               @QueryParam("parentId") Long parentId,
                               @QueryParam("offset") @DefaultValue("0") Integer offset,
                               @QueryParam("limit") @DefaultValue("10") @Max(1000) Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("city", parentId);
        filter.addAttribute(StreetStrategy.NAME, query);
        filter.setFirst(offset);
        filter.setCount(limit);

        return Response.ok(streetStrategy.getList(filter).stream()
                .map(d -> new AddressModel(d.getObjectId(), d.getParentId(), d.getValueId(StreetStrategy.STREET_TYPE),
                        d.getStringValue(StreetStrategy.STREET_CODE), d.getStringMap(StreetStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("street/size")
    @ApiOperation(value = "Get streets count by query")
    public Response getStreetsCount(@QueryParam("query") String query,
                               @QueryParam("parentId") Long parentId){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setParent("city", parentId);
        filter.addAttribute(StreetStrategy.NAME, query);

        return Response.ok(streetStrategy.getCount(filter)).build();
    }

    private BuildingModel getBuildingObject(Building b){
        DomainObject a = b.getAccompaniedAddress();

        BuildingModel building = new BuildingModel(b.getObjectId(), a.getParentId(),
                a.getStringMap(BuildingAddressStrategy.NUMBER), a.getStringMap(BuildingAddressStrategy.CORP),
                a.getStringMap(BuildingAddressStrategy.STRUCTURE));

        if (!b.getAlternativeAddresses().isEmpty()){
            List<BuildingModel> alternatives = new ArrayList<>();
            building.setAlternatives(alternatives);

            b.getAlternativeAddresses().forEach(alt -> {
                alternatives.add(new BuildingModel(null, alt.getParentId(),
                        alt.getStringMap(BuildingAddressStrategy.NUMBER), alt.getStringMap(BuildingAddressStrategy.CORP),
                        alt.getStringMap(BuildingAddressStrategy.STRUCTURE)));
            });
        }

        return building;
    }

    @GET
    @Path("building/{id}")
    @ApiOperation(value = "Get building by id", response = BuildingModel.class)
    public Response getBuilding(@PathParam("id") Long id){
        Building building = buildingStrategy.getDomainObject(id, true);

        if (building == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(getBuildingObject(building)).build();
    }

    @GET
    @Path("building")
    @ApiOperation(value = "Get building list by query", response = BuildingModel.class, responseContainer = "List")
    public Response getBuildings(@QueryParam("query") String query,
                                 @QueryParam("parentId") Long parentId,
                                 @QueryParam("offset") @DefaultValue("0") Integer offset,
                                 @QueryParam("limit") @DefaultValue("10") Integer limit){
        DomainObjectFilter filter = new DomainObjectFilter();

        filter.addAdditionalParam(BuildingStrategy.P_NUMBER, query);
        filter.addAdditionalParam(BuildingStrategy.P_STREET, parentId);

        filter.setFirst(offset);
        filter.setCount(limit);

        return Response.ok(buildingStrategy.getList(filter).stream()
                .map(this::getBuildingObject).collect(Collectors.toList())).build();
    }

    @GET
    @Path("building/size")
    @ApiOperation(value = "Get building list by query", response = Long.class)
    public Response getBuildingsCount(@QueryParam("query") String query,
                                 @QueryParam("parentId") Long parentId){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.addAdditionalParam(BuildingStrategy.P_NUMBER, query);
        filter.addAdditionalParam(BuildingStrategy.P_STREET, parentId);

        return Response.ok(buildingStrategy.getCount(filter)).build();
    }
}
