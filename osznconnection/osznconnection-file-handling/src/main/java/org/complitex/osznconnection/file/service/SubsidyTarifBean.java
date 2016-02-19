package org.complitex.osznconnection.file.service;

import com.google.common.collect.Maps;
import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.service.file_description.RequestFileDescription;
import org.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.SUBSIDY_TARIF;

/**
 * Класс для работы с тарифами субсидий.
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.2010 18:16:17
 */
@Stateless
public class SubsidyTarifBean extends AbstractBean {
    public static final String MAPPING_NAMESPACE = SubsidyTarifBean.class.getName();
    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<AbstractRequest> abstractRequests) {
        for (AbstractRequest abstractRequest : abstractRequests) {
            sqlSession().insert(MAPPING_NAMESPACE + ".insertTarif", abstractRequest);
        }
    }

    public void delete(long requestFileId) {
        sqlSession().delete(MAPPING_NAMESPACE + ".deleteTarifs", requestFileId);
    }

    /**
     * Получить значение поля T11_CODE2 из таблицы тарифов по значению тарифа в ЦН.
     * @param T11_CS_UNI Значение тарифа, который пришел из ЦН.
     * @param osznId ОСЗН
     * @param service код услуги СЗ
     * @return Code
     */
    public String getCode2(BigDecimal T11_CS_UNI, long osznId, long userOrganizationId, int service) {
        RequestFileDescription tarifDescription = requestFileDescriptionBean.getFileDescription(SUBSIDY_TARIF);

        Map<String, Object> params = Maps.newHashMap();
        params.put("T11_CS_UNI", tarifDescription.getTypeConverter().toString(T11_CS_UNI));
        params.put("osznId", osznId);
        params.put("userOrganizationId", userOrganizationId);
        params.put("service", service);

        return (String) sqlSession().selectOne(MAPPING_NAMESPACE + ".getCode2", params);
    }

    /**
     * Получить значение поля T11_CODE3 из таблицы тарифов по значению тарифа в ЦН.
     * @param T11_CS_UNI Значение тарифа, который пришел из ЦН.
     * @param osznId ОСЗН
     * @param service код услуги СЗ
     * @return Code
     */
    public String getCode3(BigDecimal T11_CS_UNI, long osznId, long userOrganizationId, int service) {
        RequestFileDescription tarifDescription = requestFileDescriptionBean.getFileDescription(SUBSIDY_TARIF);

        Map<String, Object> params = Maps.newHashMap();
        params.put("T11_CS_UNI", tarifDescription.getTypeConverter().toString(T11_CS_UNI));
        params.put("osznId", osznId);
        params.put("userOrganizationId", userOrganizationId);
        params.put("service", service);

        return (String) sqlSession().selectOne(MAPPING_NAMESPACE + ".getCode3", params);
    }
}
