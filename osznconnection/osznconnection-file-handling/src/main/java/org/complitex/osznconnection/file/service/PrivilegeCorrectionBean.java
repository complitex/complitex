package org.complitex.osznconnection.file.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.osznconnection.file.entity.PrivilegeCorrection;

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

    public PrivilegeCorrection getPrivilegeCorrection(Long id){
        return sqlSession().selectOne(NS + ".selectPrivilegeCorrection", id);
    }

    public List<PrivilegeCorrection> getPrivilegeCorrections(FilterWrapper<PrivilegeCorrection> filterWrapper){
        return sqlSession().selectList(NS + ".selectPrivilegeCorrections", filterWrapper);
    }

    public Long getPrivilegeCorrectionCount(FilterWrapper<PrivilegeCorrection> filterWrapper){
        return sqlSession().selectOne(NS + ".selectPrivilegeCorrectionsCount", filterWrapper);
    }
}
