package ru.complitex.pspoffice.address.sync.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;
import org.mybatis.cdi.Mapper;
import ru.complitex.pspoffice.address.sync.entity.Sync;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;

/**
 * @author Ivanov Anatoliy
 */
@Mapper
public interface SyncMapper {
    @Results(id = "sync")
    @ResultType(Sync.class)
    @Select("select current_date")
    void syncResultMap(SyncCatalog syncCatalog);

    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getCompanies" +
            "(#{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callOrganizationSyncs(SyncCatalog syncCatalog);

    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getCountries" +
            "(#{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callCountrySyncs(SyncCatalog syncCatalog);

    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getRegions" +
            "(#{country}, #{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callRegionSyncs(SyncCatalog syncCatalog);

    @ResultType(Sync.class)
    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getCityTypes" +
            "(#{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callCityTypeSyncs(SyncCatalog syncCatalog);

    @ResultType(Sync.class)
    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getCities" +
            "(#{region}, #{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callCitySyncs(SyncCatalog syncCatalog);

    @ResultType(Sync.class)
    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getDistricts" +
            "(#{city}, #{cityType}, #{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callDistrictSyncs(SyncCatalog syncCatalog);

    @ResultType(Sync.class)
    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getStreetTypes" +
            "(#{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callStreetTypeSyncs(SyncCatalog syncCatalog);

    @ResultType(Sync.class)
    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getStreets" +
            "(#{city}, #{cityType}, #{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callStreetSyncs(SyncCatalog syncCatalog);

    @ResultType(Sync.class)
    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getBuildings" +
            "(#{city}, #{cityType}, #{district}, #{street}, #{streetType},  " +
            "#{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callHouseSyncs(SyncCatalog syncCatalog);

    @ResultType(Sync.class)
    @Options(statementType = StatementType.CALLABLE)
    @Select("{#{code, mode=OUT, jdbcType=INTEGER} = call comp.z$runtime_sz_utl.getFlats" +
            "(#{city}, #{cityType}, #{district}, #{street}, #{streetType}, #{house},  #{part}, " +
            "#{date}, #{syncs, mode=OUT, jdbcType=CURSOR, resultMap=sync}, #{locale})}")
    void callFlatSyncs(SyncCatalog syncCatalog);
}
