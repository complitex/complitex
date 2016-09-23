package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.ResourceUtil;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import org.complitex.osznconnection.file.service.exception.SaveException;
import org.complitex.osznconnection.file.service.privilege.PrivilegeProlongationBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 028 25.07.16.
 */
@Stateless
public class PrivilegeProlongationSaveTaskBean extends AbstractTaskBean<RequestFile>{
    private final Logger log = LoggerFactory.getLogger(PrivilegeProlongationSaveTaskBean.class);

    private final static Class RESOURCE = PrivilegeProlongationSaveTaskBean.class;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private PrivilegeProlongationBean privilegeProlongationBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        String district = null;

        DomainObject organization = osznOrganizationStrategy.getDomainObject(requestFile.getOrganizationId());

        if (organization != null && organization.getAttribute(IOrganizationStrategy.DISTRICT) != null){
            DomainObject districtObject = districtStrategy.getDomainObject(organization.getAttribute(IOrganizationStrategy.DISTRICT).getValueId());

            if (districtObject != null){
                district = districtObject.getStringValue(DistrictStrategy.NAME);
            }
        }

        List<Long> ids = privilegeProlongationBean.getPrivilegeProlongationIds(requestFile.getId());

        boolean profit = requestFile.getName().matches(".*\\.(S|s).*");

        //todo test requestFile.getBeginDate()
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.DECEMBER, 1, 0, 0, 0);

        Long collectionId = serviceProviderAdapter.createPrivilegeProlongationHeader(requestFile.getUserOrganizationId(),
                district, calendar.getTime(), requestFile.getName(), ids.size(), profit);

        if (collectionId > 0){
            List<PrivilegeProlongation> list = privilegeProlongationBean.getPrivilegeProlongationForOperation(requestFile.getId(), ids);

            serviceProviderAdapter.savePrivilegeProlongation(requestFile.getUserOrganizationId(), list);
        }else {
            switch (collectionId.intValue()){
                case -1: //Не найден р-он
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_district_not_found"), district);
                case -2: //Дублируется имя файла для заданного месяца
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_filename_duplicate"),
                            requestFile.getName(), requestFile.getBeginDate());
                case -3: //Неправильное кол-во записей в файле
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_record_count"),
                            requestFile.getLoadedRecordCount());
                case -4: //Не указана зависимость от дохода
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_profit"));
                case -5: //Не указан месяц файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_month"));
                case -6: //Не указано имя файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_filename"));
            }
        }

        return true;
    }
}
