package org.complitex.osznconnection.file.service;

import org.complitex.common.entity.Cursor;
import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.AccountDetail;
import org.complitex.osznconnection.file.service_provider.CalculationCenterBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.service_provider.exception.UnknownAccountNumberTypeException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Date;
import java.util.List;

/**
 * @author Artem
 */
@Stateless
public class LookupBean extends AbstractBean {
    @EJB
    private AddressService addressService;

    @EJB
    private CalculationCenterBean calculationCenterBean;

    @EJB
    private ServiceProviderAdapter adapter;

    /**
     * Разрешить исходящий в ЦН адрес по схеме "локальная адресная база -> адрес центра начислений"
     * Делегирует всю работу AddressService.resolveOutgoingAddress().
     * @param request
     */

    public void resolveOutgoingAddress(AbstractAccountRequest request, long userOrganizationId) {
        addressService.resolveOutgoingAddress(request, calculationCenterBean.getContextWithAnyCalculationCenter(userOrganizationId));
    }

    /**
     * Получить детальную информацию о клиентах ЦН.
     * Вся работа по поиску делегируется адаптеру взаимодействия с ЦН.
     * См. org.complitex.osznconnection.file.calculation.adapter.DefaultCalculationCenterAdapter.acquireAccountCorrectionDetails()
     */
    public Cursor<AccountDetail> getAccountDetails(String district, String streetType, String street,
            String buildingNumber, String buildingCorp, String apartment, Date date, long userOrganizationId) throws DBException {
        return adapter.getAccountDetails(calculationCenterBean.getContextWithAnyCalculationCenter(userOrganizationId).getDataSource(),
                district, streetType, street, buildingNumber, buildingCorp, apartment, date);
    }


    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<AccountDetail> acquireAccountDetailsByAccount(AbstractRequest request, String district, String account,
            long userOrganizationId) throws DBException, UnknownAccountNumberTypeException {
        return adapter.acquireAccountDetailsByAccount(calculationCenterBean.getContextWithAnyCalculationCenter(userOrganizationId),
                request, district, account);
    }

    public List<AccountDetail> getAccountDetailsByFio(Long userOrganizationId, String districtName,
                                                      String servicingOrganizationCode, String lastName, String firstName,
                                                      String middleName, Date date) throws DBException {
        String dataSource = calculationCenterBean.getContextWithAnyCalculationCenter(userOrganizationId).getDataSource();

        return adapter.getAccountDetailsByFio(dataSource, districtName, servicingOrganizationCode,
                lastName, firstName, middleName, date);
    }
}
