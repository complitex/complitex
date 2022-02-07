package ru.complitex.osznconnection.file.service.subsidy;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyTarif;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescription;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.complitex.osznconnection.file.entity.RequestFileType.SUBSIDY_TARIF;

/**
 * Класс для работы с тарифами субсидий.
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.2010 18:16:17
 */
@Stateless
public class SubsidyTarifBean extends AbstractBean {
    public static final String NS = SubsidyTarifBean.class.getName();

    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;

    public void save(List<SubsidyTarif> subsidyTarifList) {
        for (SubsidyTarif subsidyTarif : subsidyTarifList) {
            sqlSession().insert(NS + ".insertTarif", subsidyTarif);
        }
    }

    public List<SubsidyTarif> getSubsidyTarifs(FilterWrapper<SubsidyTarif> filter){
        return sqlSession().selectList(NS + ".selectTarifs", filter);
    }

    public Long getSubsidyTarifsCount(FilterWrapper<SubsidyTarif> filter){
        return sqlSession().selectOne(NS + ".selectTarifsCount", filter);
    }

    public void delete(long requestFileId) {
        sqlSession().delete(NS + ".deleteTarifs", requestFileId);
    }

    /**
     * Получить значение поля T11_CODE2 из таблицы тарифов по значению тарифа в ЦН.
     * @param T11_CS_UNI Значение тарифа, который пришел из ЦН.
     * @param organizationId ОСЗН
     * @param service код услуги СЗ
     * @return Code
     */
    public String getCode2(BigDecimal T11_CS_UNI, long organizationId, long userOrganizationId, int service, Date date) {
        RequestFileDescription tarifDescription = requestFileDescriptionBean.getFileDescription(SUBSIDY_TARIF);

        Map<String, Object> params = new HashMap<>();
        params.put("T11_CS_UNI", tarifDescription.getTypeConverter().toString(T11_CS_UNI));
        params.put("organizationId", organizationId);
        params.put("userOrganizationId", userOrganizationId);
        params.put("service", service);
        params.put("date", date);

        return (String) sqlSession().selectOne(NS + ".getCode2", params);
    }

    public Integer getCode2Apartment(long organizationId, long userOrganizationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("organizationId", organizationId);
        params.put("userOrganizationId", userOrganizationId);

        return sqlSession().selectOne(NS + ".getCode2Apartment", params);
    }

    /**
     * Получить значение поля T11_CODE3 из таблицы тарифов по значению тарифа в ЦН.
     * @param T11_CS_UNI Значение тарифа, который пришел из ЦН.
     * @param organizationId ОСЗН
     * @param service код услуги СЗ
     * @return Code
     */
    public String getCode3(BigDecimal T11_CS_UNI, long organizationId, long userOrganizationId, int service, Date date) {
        RequestFileDescription tarifDescription = requestFileDescriptionBean.getFileDescription(SUBSIDY_TARIF);

        Map<String, Object> params = new HashMap<>();
        params.put("T11_CS_UNI", tarifDescription.getTypeConverter().toString(T11_CS_UNI));
        params.put("organizationId", organizationId);
        params.put("userOrganizationId", userOrganizationId);
        params.put("service", service);
        params.put("date", date);

        return (String) sqlSession().selectOne(NS + ".getCode3", params);
    }
}
