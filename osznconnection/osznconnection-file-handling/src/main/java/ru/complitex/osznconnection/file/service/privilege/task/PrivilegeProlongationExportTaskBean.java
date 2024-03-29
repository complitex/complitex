package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.address.strategy.district.DistrictStrategy;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.SaveException;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeProlongationBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 028 25.07.16.
 */
@Stateless
public class PrivilegeProlongationExportTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(PrivilegeProlongationExportTaskBean.class);

    private final static Class RESOURCE = PrivilegeProlongationExportTaskBean.class;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private PrivilegeProlongationBean privilegeProlongationBean;

    @EJB
    private RequestFileBean requestFileBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        requestFile.setStatus(RequestFileStatus.EXPORTING);
        requestFileBean.save(requestFile);

        String district = osznOrganizationStrategy.getDistrict(requestFile.getOrganizationId());

        List<Long> ids = privilegeProlongationBean.getPrivilegeProlongationIds(requestFile.getId());

        boolean profit = requestFile.getName().matches(".*\\.(S|s).*");

        Date date = requestFile.getBeginDate();

        String zheuCode =  osznOrganizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long collectionId = serviceProviderAdapter.createPrivilegeProlongationHeader(requestFile.getUserOrganizationId(),
                district, zheuCode, date, requestFile.getName(), ids.size(), profit);

        if (collectionId == null){
            throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_collection_id"));
        }

        if (collectionId > 0){
            List<PrivilegeProlongation> list = privilegeProlongationBean.getPrivilegeProlongationForOperation(requestFile.getId(), ids);

            list.forEach(p -> p.getDbfFields().put("COLLECTION_ID", collectionId));

            serviceProviderAdapter.exportPrivilegeProlongation(requestFile.getUserOrganizationId(), list);

            requestFile.setStatus(RequestFileStatus.EXPORTED);
            requestFileBean.save(requestFile);
        }else {
            requestFile.setStatus(RequestFileStatus.EXPORT_ERROR);
            requestFileBean.save(requestFile);

            //noinspection Duplicates
            switch (collectionId.intValue()){
                case -5: //Не найден р-он
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_district_not_found"),
                            requestFile.getFullName(), district);
                case -15: //Дублируется имя файла для заданного месяца
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_filename_duplicate"),
                            requestFile.getFullName(), requestFile.getBeginDate());
                case -16: //Неправильное кол-во записей в файле
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_record_count"),
                            requestFile.getFullName(), requestFile.getLoadedRecordCount());
                case -17: //Не указана зависимость от дохода
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_no_dependency_on_income"),
                            requestFile.getFullName());
                case -18: //Не указан месяц файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_month"),
                            requestFile.getFullName());
                case -19: //Не указано имя файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_filename"),
                            requestFile.getFullName());
                case -20: //Не определена организация
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_organization_undefined"),
                            requestFile.getFullName(), zheuCode);
                default:
                    throw new SaveException("Код ошибки {0}", collectionId);
            }
        }

        return true;
    }
}
