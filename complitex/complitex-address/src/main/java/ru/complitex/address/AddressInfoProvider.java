package ru.complitex.address;

import ru.complitex.common.util.EjbBeanLocator;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Artem
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class AddressInfoProvider {

    public static final String ADDRESS_INFO_BEAN_NAME = "AddressInfo";
    private AddressInfo addressInfo;

    @PostConstruct
    private void init() {
        addressInfo = EjbBeanLocator.getBean(ADDRESS_INFO_BEAN_NAME, true);
        if (addressInfo == null) {
            addressInfo = new DefaultAddressInfo();
        }
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }
}
