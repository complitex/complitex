package ru.flexpay.eirc;

import org.complitex.common.EjbTestBeanLocator;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.executor.ExecuteException;
import org.junit.Test;
import ru.flexpay.eirc.eirc_account.entity.EircAccount;
import ru.flexpay.eirc.eirc_account.service.EircAccountBean;
import ru.flexpay.eirc.registry.entity.*;
import ru.flexpay.eirc.registry.service.RegistryBean;
import ru.flexpay.eirc.registry.service.RegistryRecordBean;
import ru.flexpay.eirc.registry.service.TransitionNotAllowed;
import ru.flexpay.eirc.registry.service.handle.RegistryHandler;
import ru.flexpay.eirc.service_provider_account.entity.CashPayment;
import ru.flexpay.eirc.service_provider_account.entity.CashlessPayment;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.flexpay.eirc.service_provider_account.service.CashPaymentBean;
import ru.flexpay.eirc.service_provider_account.service.CashlessPaymentBean;
import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountBean;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Pavel Sknar
 */
public class TestPaymentAttribute {

    @Test
    public void testFull() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {
        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {
            final Context context = container.getContext();

            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");
            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");
            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");
            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");
            CashlessPaymentBean cashlessPaymentBean = EjbTestBeanLocator.getBean(context, "CashlessPaymentBean");
            CashPaymentBean cashPaymentBean = EjbTestBeanLocator.getBean(context, "CashPaymentBean");


            Registry registryPayments = null;
            try {
                Registry registry = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");
                registryBean.delete(registry);
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                    List<CashlessPayment> cashlessPayments = cashlessPaymentBean.getFinancialAttributes(FilterWrapper.of(new CashlessPayment(serviceProviderAccounts.get(0))), true);
                    assertEquals(0, cashlessPayments.size());
                    List<CashPayment> cashPayments = cashPaymentBean.getFinancialAttributes(FilterWrapper.of(new CashPayment(serviceProviderAccounts.get(0))), true);
                    assertEquals(0, cashPayments.size());
                }

                registryPayments = RegistryTestUtil.processingRegistry(context, "ree_payments.txt");
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                    List<CashlessPayment> cashlessPayments = cashlessPaymentBean.getFinancialAttributes(FilterWrapper.of(new CashlessPayment(serviceProviderAccounts.get(0))), true);
                    List<CashPayment> cashPayments = cashPaymentBean.getFinancialAttributes(FilterWrapper.of(new CashPayment(serviceProviderAccounts.get(0))), true);
                    assertEquals(1, cashlessPayments.size() + cashPayments.size());
                }
                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryPayments.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.CASHLESS_PAYMENT.equals(recordContainer.getType())) {
                            assertTrue(cashlessPaymentBean.financialAttributeExists(recordContainer.getId()));
                        } else if (ContainerType.CASH_PAYMENT.equals(recordContainer.getType())) {
                            assertTrue(cashPaymentBean.financialAttributeExists(recordContainer.getId()));
                        }
                    }
                }

                for (RegistryRecordData record : records) {
                    assertTrue(registryHandler.rollbackRegistryRecord(registry, record));
                }

                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryPayments.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.CASHLESS_PAYMENT.equals(recordContainer.getType())) {
                            assertFalse(cashlessPaymentBean.financialAttributeExists(recordContainer.getId()));
                        } else if (ContainerType.CASH_PAYMENT.equals(recordContainer.getType())) {
                            assertFalse(cashPaymentBean.financialAttributeExists(recordContainer.getId()));
                        }
                    }
                }

            } finally {
                try {
                    if (registryPayments != null) {
                        registryBean.delete(registryPayments);
                    }
                } catch (Throwable th) {
                    //
                }
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                for (EircAccount eircAccount : eircAccounts) {
                    try {
                        eircAccountBean.archive(eircAccount);
                    } catch (Throwable th) {
                        //
                    }
                }

            }
        }
    }
}
