package ru.flexpay.eirc;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.test.EjbTestBeanLocator;
import org.junit.Test;
import ru.flexpay.eirc.eirc_account.entity.EircAccount;
import ru.flexpay.eirc.eirc_account.service.EircAccountBean;
import ru.flexpay.eirc.registry.entity.*;
import ru.flexpay.eirc.registry.service.AbstractFinishCallback;
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
public class TestAccount {

    @Test
    public void testOpen() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {
        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {
            final Context context = container.getContext();

            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");
            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");
            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");
            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");


            Registry registry = null;
            try {
                registry = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registry.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNotNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

                for (RegistryRecordData record : records) {
                    assertTrue(registryHandler.rollbackRegistryRecord(registry, record));
                }

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(0, eircAccounts.size());
                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registry.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

            } finally {
                try {
                    if (registry != null) {
                        registryBean.delete(registry);
                    }
                } catch (Throwable th) {
                    //
                }
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                for (EircAccount eircAccount : eircAccounts) {
                    try {
                        eircAccountBean.delete(eircAccount);
                    } catch (Throwable th) {
                        System.err.print("Can not delete eirc account: " + th.getLocalizedMessage());
                        assertTrue(false);
                    }
                }

            }
        }
    }

    @Test
    public void testOpen2() throws Exception {
        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {
            final Context context = container.getContext();

            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");
            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");
            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");
            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");


            Registry registry = null;
            try {
                registry = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registry.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNotNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

                AbstractFinishCallback finishCallback = RegistryTestUtil.getFinishCallback();
                List<Registry> registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registry.getId())));
                assertEquals(1, registries.size());
                registry = registries.get(0);
                registryHandler.rollbackRegistryRecords(registry, records, RegistryTestUtil.getMessengerInstance(), finishCallback);
                while (!finishCallback.isCompleted()) {
                    Thread.sleep(100L);
                }

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(0, eircAccounts.size());
                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registry.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

            } finally {
                try {
                    if (registry != null) {
                        registryBean.delete(registry);
                    }
                } catch (Throwable th) {
                    //
                }
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                for (EircAccount eircAccount : eircAccounts) {
                    try {
                        eircAccountBean.delete(eircAccount);
                    } catch (Throwable th) {
                        System.err.print("Can not delete eirc account: " + th.getLocalizedMessage());
                        assertTrue(false);
                    }
                }

            }
        }
    }

    @Test
    public void testClose() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {
        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {
            final Context context = container.getContext();

            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");
            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");
            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");
            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");


            Registry registryOpen = null;
            Registry registryClose = null;
            try {
                registryOpen = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNotNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

                registryClose = RegistryTestUtil.processingRegistry(context, "ree_close_account.txt");
                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(0, eircAccounts.size());

                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryClose.getId())));
                for (RegistryRecordData record : records) {
                    assertTrue(registryHandler.rollbackRegistryRecord(registryClose, record));
                }

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())));
                for (RegistryRecordData record : records) {
                    assertTrue(registryHandler.rollbackRegistryRecord(registryOpen, record));
                }

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(0, eircAccounts.size());
                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

            } finally {
                try {
                    if (registryClose != null) {
                        registryBean.delete(registryClose);
                    }
                    if (registryOpen != null) {
                        registryBean.delete(registryOpen);
                    }
                } catch (Throwable th) {
                    //
                }
            }
        }
    }

    @Test
    public void testClose2() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {
        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {
            final Context context = container.getContext();

            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");
            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");
            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");
            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");


            Registry registryOpen = null;
            Registry registryClose = null;
            try {
                registryOpen = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNotNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

                registryClose = RegistryTestUtil.processingRegistry(context, "ree_close_account.txt");
                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(0, eircAccounts.size());

                AbstractFinishCallback finishCallback = RegistryTestUtil.getFinishCallback();
                registryHandler.rollbackRegistry(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryClose.getId())),
                        RegistryTestUtil.getMessengerInstance(), finishCallback);
                while (!finishCallback.isCompleted()) {
                    Thread.sleep(100L);
                }

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                registryHandler.rollbackRegistry(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())),
                        RegistryTestUtil.getMessengerInstance(), finishCallback);
                while (!finishCallback.isCompleted()) {
                    Thread.sleep(100L);
                }

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(0, eircAccounts.size());
                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

            } finally {
                try {
                    if (registryClose != null) {
                        registryBean.delete(registryClose);
                    }
                    if (registryOpen != null) {
                        registryBean.delete(registryOpen);
                    }
                } catch (Throwable th) {
                    //
                }
            }
        }
    }

    @Test
    public void testCloseWithSaldoOut1() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {
        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {
            final Context context = container.getContext();

            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");
            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");
            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");
            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");
            SaldoOutBean saldoOutBean = EjbTestBeanLocator.getBean(context, "SaldoOutBean");
            ChargeBean chargeBean = EjbTestBeanLocator.getBean(context, "ChargeBean");

            Registry registryOpen = null;
            Registry registryClose = null;
            Registry registryFinancial1 = null;
            Registry registryFinancial2 = null;
            Registry registryFinancial3 = null;
            try {
                registryOpen = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");
                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.OPEN_ACCOUNT.equals(recordContainer.getType())) {
                            assertNotNull(serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(recordContainer.getId()));
                        }
                    }
                }

                registryFinancial1 = RegistryTestUtil.processingRegistry(context, "ree_financial.txt");
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                    List<SaldoOut> saldoOuts = saldoOutBean.getFinancialAttributes(FilterWrapper.of(new SaldoOut(serviceProviderAccounts.get(0))), true);
                    assertEquals(1, saldoOuts.size());
                }
                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryFinancial1.getId())));
                for (RegistryRecordData record : records) {
                    for (Container recordContainer : record.getContainers()) {
                        if (ContainerType.SALDO_OUT.equals(recordContainer.getType())) {
                            assertTrue(saldoOutBean.financialAttributeExists(recordContainer.getId()));
                        } else if (ContainerType.CHARGE.equals(recordContainer.getType())) {
                            assertTrue(chargeBean.financialAttributeExists(recordContainer.getId()));
                        }
                    }
                }

                registryClose = RegistryTestUtil.processingRegistry(context, "ree_close_account.txt");
                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());

                registryFinancial2 = RegistryTestUtil.processingRegistry(context, "ree_financial2.txt");
                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(2, eircAccounts.size());

                registryFinancial3 = RegistryTestUtil.processingRegistry(context, "ree_financial3.txt");
                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(0, eircAccounts.size());

                /*
                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryClose.getId())));
                for (RegistryRecordData record : records) {
                    assertTrue(registryHandler.rollbackRegistryRecord(registryClose, record));
                }

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                for (EircAccount eircAccount : eircAccounts) {
                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));
                    assertEquals(1, serviceProviderAccounts.size());
                }

                boolean exception = false;
                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryOpen.getId())));
                try {
                    for (RegistryRecordData record : records) {
                        // TODO по идее не должен давать откатить, т.к. есть финансовые операции по счету, но по факту сейчас эксепшен ловим
                        assertFalse(registryHandler.rollbackRegistryRecord(registryOpen, record));
                    }
                } catch (Exception ex) {
                    exception = true;
                }
                assertTrue("Must exception", exception);

                eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                assertEquals(3, eircAccounts.size());
                */
            } finally {
                try {
                    if (registryClose != null) {
                        registryBean.delete(registryClose);
                    }
                    if (registryOpen != null) {
                        registryBean.delete(registryOpen);
                    }
                    if (registryFinancial1 != null) {
                       registryBean.delete(registryFinancial1);
                    }
                    if (registryFinancial2 != null) {
                       registryBean.delete(registryFinancial2);
                    }
                    if (registryFinancial3 != null) {
                       registryBean.delete(registryFinancial3);
                    }
                    List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));
                    for (EircAccount eircAccount : eircAccounts) {
                        eircAccountBean.archive(eircAccount);
                    }
                } catch (Throwable th) {
                    //
                }
            }
        }
    }
}
