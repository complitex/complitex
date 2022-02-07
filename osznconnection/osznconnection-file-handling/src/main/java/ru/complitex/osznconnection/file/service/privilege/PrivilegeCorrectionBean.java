package ru.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.ImmutableMap;
import ru.complitex.correction.service.CorrectionBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Класс для работы с коррекциями привилегий.
 * @author Artem
 */
@Stateless
public class PrivilegeCorrectionBean extends CorrectionBean {
    private static final String NS = PrivilegeCorrectionBean.class.getName();

    /**
     * Найти id внутреннего объекта системы(привилегии) в таблице коррекций привилегий по коду коррекции(organizationCode) и организации(organizationId)
     * @param organizationCode
     * @param calculationCenterId
     * @return
     */

    public Long findInternalPrivilege(String organizationCode, long calculationCenterId) {
        Map<String, Object> params = ImmutableMap.<String, Object>of("code", organizationCode, "organizationId", calculationCenterId);
        List<Long> ids = sqlSession().selectList(NS + ".findInternalPrivilege", params);
        if (ids != null && !ids.isEmpty()) {
            return ids.get(0);
        }
        return null;
    }

    /**
     * Найти код коррекции в таблице коррекций привилегий по id внутреннего объекта системы(привилегии) и организации.
     * @param objectId
     * @param osznId
     * @return
     */

    public String findPrivilegeCode(long objectId, long osznId, long userOrganizationId) {
        Map<String, Long> params = ImmutableMap.of("objectId", objectId, "organizationId", osznId,
                "userOrganizationId", userOrganizationId);
        List<String> codes = sqlSession().selectList(NS + ".findPrivilegeCode", params);
        if (codes != null && !codes.isEmpty()) {
            return codes.get(0);
        }
        return null;
    }
}
