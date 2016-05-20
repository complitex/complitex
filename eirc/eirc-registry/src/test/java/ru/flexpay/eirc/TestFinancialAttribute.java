package ru.flexpay.eirc;

import org.complitex.common.EjbTestBeanLocator;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.exception.ExecuteException;
import org.junit.Test;
import ru.flexpay.eirc.eirc_account.entity.EircAccount;
import ru.flexpay.eirc.eirc_account.service.EircAccountBean;
import ru.flexpay.eirc.registry.entity.*;
import ru.flexpay.eirc.registry.service.RegistryBean;
import ru.flexpay.eirc.registry.service.RegistryRecordBean;
import ru.flexpay.eirc.registry.service.TransitionNotAllowed;
import ru.flexpay.eirc.registry.service.handle.RegistryHandler;
import ru.flexpay.eirc.service_provider_account.entity.SaldoOut;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.flexpay.eirc.service_provider_account.service.ChargeBean;
import ru.flexpay.eirc.service_provider_account.service.SaldoOutBean;
import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountBean;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Pavel Sknar
 */
public class TestFinancialAttribute {

    @Test
    public void testFull() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {
        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {
            final Context context = container.getContext();

            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");
            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");
            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");
            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");
            SaldoOutBean saldoOutBean = EjbTestBeanLocator.getBean(context, "SaldoOutBean");
            ChargeBean chargeBean = EjbTestBeanLocator.getBean(context, "ChargeBean");


            Registry registryFinancial = null;
            try {
                Registry registry = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");
                registryBean.delete(registry);
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                    List<SaldoOut> saldoOuts = saldoOutBean.getFinancialAttributes(FilterWrapper.of(new SaldoOut(serviceProviderAccounts.get(0))), true);
                    assertEquals(0, saldoOuts.size());
                }

                registryFinancial = RegistryTestUtil.processingRegistry(context, "ree_financial.txt");
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                    List<SaldoOut> saldoOuts = saldoOutBean.getFinancialAttributes(FilterWrapper.of(new SaldoOut(serviceProviderAccounts.get(0))), true);
                    assertEquals(1, saldoOuts.size());
                }
                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryFinancial.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.SALDO_OUT.equals(recordContainer.getType())) {
                            assertTrue(saldoOutBean.financialAttributeExists(recordContainer.getId()));
                        } else if (ContainerType.CHARGE.equals(recordContainer.getType())) {
                            assertTrue(chargeBean.financialAttributeExists(recordContainer.getId()));
                        }
                    }
                }

                for (RegistryRecordData record : records) {
                    assertTrue(registryHandler.rollbackRegistryRecord(registry, record));
                }

                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryFinancial.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.SALDO_OUT.equals(recordContainer.getType())) {
                            assertFalse(saldoOutBean.financialAttributeExists(recordContainer.getId()));
                        } else if (ContainerType.CHARGE.equals(recordContainer.getType())) {
                            assertFalse(chargeBean.financialAttributeExists(recordContainer.getId()));
                        }
                    }
                }

            } finally {
                try {
                    if (registryFinancial != null) {
                        registryBean.delete(registryFinancial);
                    }
                } catch (Throwable th) {
                    //
                }
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                for (EircAccount eircAccount : eircAccounts) {
                    try {
                        List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                        for (ServiceProviderAccount serviceProviderAccount : serviceProviderAccounts) {
                            serviceProviderAccountBean.delete(serviceProviderAccount);
                        }
                        eircAccountBean.delete(eircAccount);
                    } catch (Throwable th) {
                        System.err.print("Can not delete eirc account: " + th.getLocalizedMessage());
                        assertTrue(false);
                    }
                }

            }
        }
    }
}
