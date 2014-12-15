package ru.flexpay.eirc;import com.beust.jcommander.internal.Maps;import org.complitex.common.EjbTestBeanLocator;import org.complitex.common.entity.Attribute;import org.complitex.common.entity.DomainObject;import org.complitex.common.entity.FilterWrapper;import org.complitex.common.service.executor.ExecuteException;import org.complitex.common.util.CloneUtil;import org.junit.Test;import ru.flexpay.eirc.eirc_account.entity.EircAccount;import ru.flexpay.eirc.eirc_account.service.EircAccountBean;import ru.flexpay.eirc.registry.entity.Container;import ru.flexpay.eirc.registry.entity.Registry;import ru.flexpay.eirc.registry.entity.RegistryRecord;import ru.flexpay.eirc.registry.entity.RegistryRecordData;import ru.flexpay.eirc.registry.service.RegistryBean;import ru.flexpay.eirc.registry.service.RegistryRecordBean;import ru.flexpay.eirc.registry.service.TransitionNotAllowed;import ru.flexpay.eirc.registry.service.handle.RegistryHandler;import ru.flexpay.eirc.registry.service.handle.changing.ObjectChangingBean;import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccountAttribute;import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountBean;import ru.flexpay.eirc.service_provider_account.strategy.ServiceProviderAccountStrategy;import javax.ejb.embeddable.EJBContainer;import javax.naming.Context;import java.io.IOException;import java.util.List;import java.util.Map;import static org.junit.Assert.*;/** * @author Pavel Sknar */public class TestSPAAttribute {    @Test    public void testFull() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {            final Context context = container.getContext();            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");            ObjectChangingBean objectChangingBean = EjbTestBeanLocator.getBean(context, "ObjectChangingBean");            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");            ServiceProviderAccountStrategy serviceProviderAccountStrategy = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountStrategy");            Registry registryNewAttr = null;            Registry registryChangeAttr = null;            try {                Registry registry = RegistryTestUtil.processingRegistry(context, "ree_open_account.txt");                registryBean.delete(registry);                registryNewAttr = RegistryTestUtil.processingRegistry(context, "ree_new_attr.txt");                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));                assertEquals(3, eircAccounts.size());                Map<Long, DomainObject> oldObjectAttributes = Maps.newHashMap();                for (EircAccount eircAccount : eircAccounts) {                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));                    assertEquals(1, serviceProviderAccounts.size());                    DomainObject attributes = serviceProviderAccountStrategy.findById(serviceProviderAccounts.get(0).getId(), true);                    long countApplyAttributes = 0;                    oldObjectAttributes.put(attributes.getObjectId(), CloneUtil.cloneObject(attributes));                    for (Attribute attribute : attributes.getAttributes()) {                        if (((ServiceProviderAccountAttribute)attribute).getPkId() != null) {                            countApplyAttributes++;                            assertNull(attribute.getEndDate());                        }                    }                    assertEquals(1, countApplyAttributes);                }                registryChangeAttr = RegistryTestUtil.processingRegistry(context, "ree_change_attr.txt");                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryChangeAttr.getId())));                for (EircAccount eircAccount : eircAccounts) {                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));                    assertEquals(1, serviceProviderAccounts.size());                    DomainObject attributes = serviceProviderAccountStrategy.findById(serviceProviderAccounts.get(0).getId(), true);                    long countApplyAttributes = 0;                    DomainObject oldAttributes = oldObjectAttributes.get(attributes.getObjectId());                    for (Attribute attribute : attributes.getAttributes()) {                        if (((ServiceProviderAccountAttribute)attribute).getPkId() != null) {                            countApplyAttributes++;                            assertNull(attribute.getEndDate());                            assertNotEquals(attribute.getStringValue(), oldAttributes.getAttribute(attribute.getAttributeTypeId()).getStringValue());                        }                    }                    assertEquals(1, countApplyAttributes);                }                for (RegistryRecordData record : records) {                    for (Container container1 : record.getContainers()) {                        assertNotNull(objectChangingBean.findChanging(container1.getId()));                    }                }                for (RegistryRecordData record : records) {                    assertTrue(registryHandler.rollbackRegistryRecord(registry, record));                }                for (RegistryRecordData record : records) {                    for (Container container1 : record.getContainers()) {                        assertNull(objectChangingBean.findChanging(container1.getId()));                    }                }                for (EircAccount eircAccount : eircAccounts) {                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));                    assertEquals(1, serviceProviderAccounts.size());                    DomainObject attributes = serviceProviderAccountStrategy.findById(serviceProviderAccounts.get(0).getId(), true);                    long countApplyAttributes = 0;                    DomainObject oldAttributes = oldObjectAttributes.get(attributes.getObjectId());                    for (Attribute attribute : attributes.getAttributes()) {                        if (((ServiceProviderAccountAttribute)attribute).getPkId() != null) {                            countApplyAttributes++;                            assertNull(attribute.getEndDate());                            assertEquals(attribute.getStringValue(), oldAttributes.getAttribute(attribute.getAttributeTypeId()).getStringValue());                        }                    }                    assertEquals(1, countApplyAttributes);                }                records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryNewAttr.getId())));                for (RegistryRecordData record : records) {                    for (Container container1 : record.getContainers()) {                        assertNotNull(objectChangingBean.findChanging(container1.getId()));                    }                }                for (RegistryRecordData record : records) {                    assertTrue(registryHandler.rollbackRegistryRecord(registry, record));                }            } finally {                try {                    if (registryNewAttr != null) {                        registryBean.delete(registryNewAttr);                    }                } catch (Throwable th) {                    //                }                try {                    if (registryChangeAttr != null) {                        registryBean.delete(registryChangeAttr);                    }                } catch (Throwable th) {                    //                }                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));                for (EircAccount eircAccount : eircAccounts) {                    try {                        List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));                        for (ServiceProviderAccount serviceProviderAccount : serviceProviderAccounts) {                            serviceProviderAccountBean.delete(serviceProviderAccount);                        }                        eircAccountBean.delete(eircAccount);                    } catch (Throwable th) {                        System.err.print("Can not delete eirc account: " + th.getLocalizedMessage());                        assertTrue(false);                    }                }            }        }    }}