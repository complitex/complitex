package ru.complitex.osznconnection.file.service.warning;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.*;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Artem
 */
@Stateless
public class RequestWarningBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = RequestWarningBean.class.getName();


    public void save(RequestWarning requestWarning) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertWarning", requestWarning);
        for (RequestWarningParameter parameter : requestWarning.getParameters()) {
            parameter.setRequestWarningId(requestWarning.getId());
            sqlSession().insert(MAPPING_NAMESPACE + ".insertParameter", parameter);
        }
    }


    public List<RequestWarning> getWarnings(long requestId, RequestFileType requestFileType) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestId", requestId);
        params.put("requestFileType", requestFileType);
        List<RequestWarning> warnings = sqlSession().selectList(MAPPING_NAMESPACE + ".getWarnings", params);
        return warnings;
    }

    public void delete(long requestFileId, RequestFileType requestFileType) {
        List<Long> warningsIds = getWarningIdsByFile(requestFileType, requestFileId);
        for (Long warningId : warningsIds) {
            sqlSession().delete(MAPPING_NAMESPACE + ".deleteParameter", warningId);
            sqlSession().delete(MAPPING_NAMESPACE + ".deleteWarning", warningId);
        }
    }

    public void delete(AbstractRequest request) {
        List<Long> warningsIds = getWarningIdsByRequest(request);
        for (Long warningId : warningsIds) {
            sqlSession().delete(MAPPING_NAMESPACE + ".deleteParameter", warningId);
            sqlSession().delete(MAPPING_NAMESPACE + ".deleteWarning", warningId);
        }
    }

    /**
     * Helper methods
     */

    public void save(RequestFileType requestFileType, long requestId, RequestWarningStatus warningStatus, RequestWarningParameter... parameters) {
        RequestWarning warning = new RequestWarning(requestId, requestFileType, warningStatus);
        if (parameters != null) {
            warning.setParameters(Lists.newArrayList(parameters));
        }
        save(warning);
    }


    protected List<Long> getWarningIdsByFile(RequestFileType requestFileType, Long requestFileId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", requestFileId);
        params.put("requestFileType", requestFileType);
        params.put("requestTableName", requestFileType.name().toLowerCase());
        return sqlSession().selectList(MAPPING_NAMESPACE + ".getWarningIdsByFile", params);
    }

    protected List<Long> getWarningIdsByRequest(AbstractRequest request) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestId", request.getId());
        params.put("requestFileType", request.getRequestFileType());
        return sqlSession().selectList(MAPPING_NAMESPACE + ".getWarningIdsByRequest", params);
    }
}
