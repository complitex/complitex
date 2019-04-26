package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.ResourceUtil;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponse;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponseField;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponseFile;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponseFileField;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.SaveException;
import org.complitex.osznconnection.file.service.subsidy.OschadbankResponseBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankResponseFileBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 18:36
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class OschadbankResponseExportTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(OschadbankResponseExportTaskBean.class);

    private final static Class RESOURCE = OschadbankResponseExportTaskBean.class;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private OschadbankResponseFileBean oschadbankResponseFileBean;

    @EJB
    private OschadbankResponseBean oschadbankResponseBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        requestFile.setStatus(RequestFileStatus.EXPORTING);
        requestFileBean.save(requestFile);

        OschadbankResponseFile oschadbankResponseFile = oschadbankResponseFileBean.getOschadbankResponseFile(requestFile.getId());

        LocalDate beginDate = LocalDate.parse(oschadbankResponseFile.getStringField(OschadbankResponseFileField.REPORTING_PERIOD) + "-01");
        LocalDate endDate = beginDate.with(TemporalAdjusters.lastDayOfMonth());

        String zheuCode = "ЖКС";

        Long collectionId = serviceProviderAdapter.createProvHeader(requestFile.getUserOrganizationId(),
                zheuCode, DateUtil.getDate(beginDate), requestFile.getName(), "Ответ от Ощадбанка",
                oschadbankResponseFile.getStringField(OschadbankResponseFileField.PROVIDER_CODE));

        if (collectionId == null){
            throw new SaveException("collection_id is null");
        }

        if (collectionId > 0){
            oschadbankResponseBean.getOschadbankResponses(FilterWrapper.of(new OschadbankResponse(requestFile.getId())))
                    .forEach(r -> {
                        Long resultCode = serviceProviderAdapter.createProvRec(requestFile.getUserOrganizationId(), collectionId,
                                r.getStringField(OschadbankResponseField.FIO),
                                r.getStringField(OschadbankResponseField.OSCHADBANK_ACCOUNT),
                                r.getStringField(OschadbankResponseField.SERVICE_ACCOUNT),
                                getBigDecimal(r.getStringField(OschadbankResponseField.SUBSIDY_SUM)),
                                getBigDecimal(r.getStringField(OschadbankResponseField.SUM)),
                                DateUtil.getDate(beginDate), DateUtil.getDate(endDate));

                        if (resultCode != 1L){
                            log.error("error createProvRec: resultCode = " + requestFile);
                        }
                    });
        }else {
            requestFile.setStatus(RequestFileStatus.EXPORT_ERROR);
            requestFileBean.save(requestFile);

            switch (collectionId.intValue()){
                case -20: //Не определена организация
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error-20"),
                            requestFile.getFullName(), zheuCode);
                case -19: //Не указано имя файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error-19"),
                            requestFile.getFullName());
                case -18: //Не указан месяц файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error-18"),
                            requestFile.getFullName());
                case -23: //Не указано описание
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error-23"),
                            requestFile.getFullName(), requestFile.getLoadedRecordCount());
                case -24: //Не найден БИК
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error-24"),
                            requestFile.getFullName());
                default:
                    throw new SaveException("код ошибки {0}", collectionId);
            }
        }

        requestFile.setStatus(RequestFileStatus.EXPORTED);
        requestFileBean.save(requestFile);


        return true;
    }

    private BigDecimal getBigDecimal(String s){
        return s != null && !s.isEmpty() ? new BigDecimal(s) : null;
    }
}
