package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.entity.Log.EVENT;
import ru.complitex.common.oracle.OracleErrors;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.service.LogBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.keconnection.heatmeter.Module;
import ru.complitex.keconnection.heatmeter.entity.Heatmeter;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import ru.complitex.keconnection.heatmeter.entity.cursor.ComMeterCursor;
import ru.complitex.keconnection.heatmeter.entity.cursor.ExternalHeatmeter;
import ru.complitex.keconnection.heatmeter.service.exception.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.Serializable;
import java.util.*;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ExternalHeatmeterService extends AbstractBean{

    private final Logger log = LoggerFactory.getLogger(ExternalHeatmeterService.class);
    private static final String MAPPING_NAMESPACE = ExternalHeatmeterService.class.getName();
    private static final String RESOURCE_BUNDLE = ExternalHeatmeterService.class.getName();
    private static final String FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE = "Z$RUNTIME_PROV_UTL.GETHEATMETERS";

    @EJB
    private LogBean logBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    protected void logError(long heatmeterId, String key, Object... params) {
        logError(heatmeterId, ResourceUtil.getFormatString(RESOURCE_BUNDLE, key, getLocale(), params));
    }

    protected void logError(long heatmeterId, String message) {
        logBean.error(Module.NAME, ExternalHeatmeterService.class, Heatmeter.class, heatmeterId, EVENT.GETTING_DATA,
                message);
    }

    protected Locale getLocale() {
        return stringLocaleBean.getSystemLocale();
    }

    public static class ExternalHeatmetersAndStatus implements Serializable {

        public final List<ExternalHeatmeter> heatmeters;
        public final HeatmeterBindingStatus status;

        private ExternalHeatmetersAndStatus(List<ExternalHeatmeter> heatmeters, HeatmeterBindingStatus status) {
            this.heatmeters = heatmeters;
            this.status = status;
        }

        @Override
        public String toString() {
            String s = "External heatmeters: ";
            s += heatmeters != null ? heatmeters.toString() : "[]";
            s += ", status: " + status;
            return s;
        }
    }

    public ExternalHeatmetersAndStatus fetchExternalHeatmeters(String dataSource, Long heatmeterId, Integer ls,
            String organizationCode, Long buildingCode, Date date) throws DBException {
        List<ExternalHeatmeter> externalHeatmeters = null;
        HeatmeterBindingStatus status;

        Map<String, Object> params = new HashMap<>();
        params.put("pDepCode", organizationCode);
        params.put("pHouseCode", buildingCode);
        params.put("pDate", date);

        long startTime = 0;
        if (log.isDebugEnabled()) {
            startTime = System.nanoTime();
        }
        try {
            sqlSession(dataSource).selectOne(MAPPING_NAMESPACE + ".fetchExternalHeatmeter", params);
        } catch (Exception e) {
            if (!OracleErrors.isCursorClosedError(e)) {
                throw new DBException(e);
            }
        } finally {
            log.info("{}. Heatmeter id: {}, ls: {}, parameters : {}",
                    FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, heatmeterId, ls, params);
            if (log.isDebugEnabled()) {
                log.debug("{}. Time of operation: {} sec.", new Object[]{FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE,
                            (System.nanoTime() - startTime) / 1000000000F});
            }
        }

        Integer resultCode = (Integer) params.get("resultCode");
        if (resultCode == null) {
            log.error("{}. Result code is null. Heatmeter id: {}, ls: {}",
                    FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, heatmeterId, ls);
            logError(heatmeterId, "result_code_unexpected", FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, "null");
            status = HeatmeterBindingStatus.BINDING_ERROR;
        } else {
            switch (resultCode) {
                case 1:
                    //noinspection unchecked
                    externalHeatmeters = (List<ExternalHeatmeter>) params.get("externalInfo");
                    if (externalHeatmeters == null || externalHeatmeters.isEmpty()) {
                        status = HeatmeterBindingStatus.NO_EXTERNAL_HEATMETERS;
                    } else if (externalHeatmeters.size() > 1) {
                        status = HeatmeterBindingStatus.MORE_ONE_EXTERNAL_HEATMETER;
                    } else {
                        status = HeatmeterBindingStatus.BOUND;
                    }
                    break;
                case -1:
                    status = HeatmeterBindingStatus.BUILDING_NOT_FOUND;
                    break;
                case -2:
                    status = HeatmeterBindingStatus.ORGANIZATION_NOT_FOUND;
                    break;
                default:
                    log.error("{}. Unexpected result code: {}. Heatmeter id: {}, ls: {}",
                            FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, resultCode, heatmeterId, ls);
                    logError(heatmeterId, "result_code_unexpected", FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, resultCode);
                    status = HeatmeterBindingStatus.BINDING_ERROR;
            }
        }
        return new ExternalHeatmetersAndStatus(externalHeatmeters, status);
    }

    public void callComMeterCursor(ComMeterCursor comMeterCursor){
        sqlSession(comMeterCursor.getDataSource()).selectOne(MAPPING_NAMESPACE + ".getComMeterCursor", comMeterCursor);

        log.info("getComMeterCursor data: {}", comMeterCursor);
    }
}
