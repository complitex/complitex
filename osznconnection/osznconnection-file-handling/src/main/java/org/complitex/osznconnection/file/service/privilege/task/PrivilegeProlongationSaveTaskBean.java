package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * @author inheaven on 028 25.07.16.
 */
@Stateless
public class PrivilegeProlongationSaveTaskBean extends AbstractTaskBean<RequestFile>{
    private final Logger log = LoggerFactory.getLogger(PrivilegeProlongationSaveTaskBean.class);

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {

        String district = ""; //todo get district
        boolean profit = requestFile.getName().matches(".*//.(S|s).*");

        Long collectionId = serviceProviderAdapter.createPrivilegeProlongationHeader(requestFile.getUserOrganizationId(),
                district, requestFile.getBeginDate(), requestFile.getName(), requestFile.getLoadedRecordCount(), profit);

        if (collectionId > 0){

        }else {
            switch (collectionId.intValue()){
                case -1: //Не найден р-он
                    break;
                case -2: //Дублируется имя файла для заданного месяца
                    break;
                case -3: //Неправильное кол-во записей в файле
                    break;
                case -4: //Не указана зависимость от дохода
                    break;
                case -5: //Не указан месяц файла
                    break;
                case -6: //Не указано имя файла
                    break;
            }
        }

        return false;
    }
}
