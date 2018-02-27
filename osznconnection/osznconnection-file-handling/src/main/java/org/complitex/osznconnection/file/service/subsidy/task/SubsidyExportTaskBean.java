package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.ResourceUtil;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.SaveException;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyService;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 021 21.10.16.
 */
@Stateless
public class SubsidyExportTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(SubsidyExportTaskBean.class);

    private final static Class RESOURCE = SubsidyExportTaskBean.class;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private SubsidyService subsidyService;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        requestFile.setStatus(RequestFileStatus.SAVING);
        requestFileBean.save(requestFile);

        String district = null;

        DomainObject organization = osznOrganizationStrategy.getDomainObject(requestFile.getOrganizationId());

        if (organization != null && organization.getAttribute(IOrganizationStrategy.DISTRICT) != null){
            DomainObject districtObject = districtStrategy.getDomainObject(organization.getAttribute(IOrganizationStrategy.DISTRICT).getValueId());

            if (districtObject != null){
                district = districtObject.getStringValue(DistrictStrategy.NAME);
            }
        }

        List<Subsidy> list = subsidyService.getSubsidyWithSplitList(requestFile.getId());

        Date date = requestFile.getBeginDate();

        String zheuCode = osznOrganizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long collectionId = serviceProviderAdapter.createSubsHeader(requestFile.getUserOrganizationId(), district,
                zheuCode, date, requestFile.getName(), list.size());

        if (collectionId == null){
            throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_collection_id"));
        }

        if (collectionId > 0){
            list.forEach(s -> s.getDbfFields().put("COLLECTION_ID", collectionId));

            serviceProviderAdapter.exportSubsidy(requestFile.getUserOrganizationId(), list);
        }else{
            requestFile.setStatus(RequestFileStatus.SAVE_ERROR);
            requestFileBean.save(requestFile);

            //noinspection Duplicates
            switch (collectionId.intValue()){
                case -1: //Не найден р-он
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_district_not_found"), district);
                case -2: //Дублируется имя файла для заданного месяца
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_duplicate_name"), requestFile.getName());
                case -3: //Неправильное кол-во записей в файле
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_record_count"),
                            requestFile.getLoadedRecordCount());
                case -5: //Не указан месяц файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_month"));
                case -6: //Не указано имя файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_filename"));
                case -7: //Не определена организация
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_organization_undefined"), zheuCode);
                default:
                    throw new SaveException("код ошибки: " + collectionId);
            }
        }

        requestFile.setStatus(RequestFileStatus.SAVED);
        requestFileBean.save(requestFile);

        return true;
    }
}
