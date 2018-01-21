package org.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.ImmutableMap;
import org.complitex.correction.service.CorrectionBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Класс для работы с коррекциями форм власти
 * @author Artem
 */
@Stateless
public class OwnershipCorrectionBean extends CorrectionBean {
    private static final String NS = OwnershipCorrectionBean.class.getName();

    /**
     * Найти id внутреннего объекта системы(форму власти) в таблице коррекций форм власти по коррекции(correction) и организации(organizationId)
     * @param correction
     * @param organizationId
     * @return
     */

    public Long findInternalOwnership(String correction, long organizationId) {
        if (correction != null) {
            Map<String, Object> params = ImmutableMap.of("correction", correction, "organizationId", organizationId);
            List<Long> ids = sqlSession().selectList(NS + ".findInternalOwnership", params);
            if (ids != null && !ids.isEmpty()) {
                return ids.get(0);
            }
        }

        return null;
    }

    /**
     * Найти код коррекции в таблице коррекций форм власти по id внутреннего объекта системы(формы власти) и организации.
     * @param objectId
     * @param osznId
     * @return
     */

    public String findOwnershipCode(long objectId, long osznId, long userOrganizationId) {
        Map<String, Long> params = ImmutableMap.of("objectId", objectId, "organizationId", osznId,
                "userOrganizationId", userOrganizationId);
        List<String> codes = sqlSession().selectList(NS + ".findOwnershipCode", params);
        if (codes != null && !codes.isEmpty()) {
            return codes.get(0);
        }
        return null;
    }
}
