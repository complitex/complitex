package ru.flexpay.eirc;import com.beust.jcommander.internal.Maps;import org.apache.commons.lang.time.StopWatch;import org.apache.ibatis.io.Resources;import org.complitex.common.EjbTestBeanLocator;import org.complitex.common.entity.Attribute;import org.complitex.common.entity.DomainObject;import org.complitex.common.entity.FilterWrapper;import org.complitex.common.service.executor.ExecuteException;import org.complitex.common.util.CloneUtil;import org.junit.Test;import ru.flexpay.eirc.eirc_account.entity.EircAccount;import ru.flexpay.eirc.eirc_account.service.EircAccountBean;import ru.flexpay.eirc.registry.entity.*;import ru.flexpay.eirc.registry.service.*;import ru.flexpay.eirc.registry.service.handle.RegistryHandler;import ru.flexpay.eirc.registry.service.handle.changing.ServiceProviderAccountAttrChangingBean;import ru.flexpay.eirc.registry.service.link.RegistryLinker;import ru.flexpay.eirc.registry.service.parse.RegistryParser;import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccountAttribute;import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountBean;import ru.flexpay.eirc.service_provider_account.strategy.ServiceProviderAccountStrategy;import javax.ejb.embeddable.EJBContainer;import javax.naming.Context;import java.io.IOException;import java.io.InputStream;import java.util.*;import java.util.concurrent.atomic.AtomicInteger;import static org.junit.Assert.*;/** * @author Pavel Sknar */public class TestSPAAttribute {    @Test    public void testFull() throws InterruptedException, IOException, ExecuteException, TransitionNotAllowed {        try (final EJBContainer container = EjbTestBeanLocator.createEJBContainer()) {            final Context context = container.getContext();            final Locale locale = new Locale("ru");            RegistryParser registryParser = EjbTestBeanLocator.getBean(context, "RegistryParser");            RegistryLinker registryLinker = EjbTestBeanLocator.getBean(context, "RegistryLinker");            RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");            RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");            EircAccountBean eircAccountBean = EjbTestBeanLocator.getBean(context, "EircAccountBean");            RegistryRecordBean registryRecordBean = EjbTestBeanLocator.getBean(context, "RegistryRecordBean");            ServiceProviderAccountAttrChangingBean serviceProviderAccountAttrChangingBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountAttrChangingBean");            ServiceProviderAccountBean serviceProviderAccountBean = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountBean");            ServiceProviderAccountStrategy serviceProviderAccountStrategy = EjbTestBeanLocator.getBean(context, "ServiceProviderAccountStrategy");            AbstractFinishCallback finishCallback = new AbstractFinishCallback() {                private AtomicInteger counter = new AtomicInteger(0);                @Override                protected AtomicInteger getCounter() {                    return counter;                }                @Override                public void setProcessId(Long processId) {                }            };            AbstractMessenger messenger = new AbstractMessenger() {                private final String RESOURCE_BUNDLE = RegistryMessenger.class.getName();                Queue<IMessage> messages = new Queue<IMessage>() {                    @Override                    public boolean add(IMessage iMessage) {                        System.out.println(iMessage.getLocalizedString(locale));                        return true;                    }                    @Override                    public boolean offer(IMessage iMessage) {                        return false;                    }                    @Override                    public IMessage remove() {                        return null;                    }                    @Override                    public IMessage poll() {                        return null;                    }                    @Override                    public IMessage element() {                        return null;                    }                    @Override                    public IMessage peek() {                        return null;                    }                    @Override                    public int size() {                        return 0;                    }                    @Override                    public boolean isEmpty() {                        return false;                    }                    @Override                    public boolean contains(Object o) {                        return false;                    }                    @Override                    public Iterator<IMessage> iterator() {                        return null;                    }                    @Override                    public Object[] toArray() {                        return new Object[0];                    }                    @Override                    public <T> T[] toArray(T[] a) {                        return null;                    }                    @Override                    public boolean remove(Object o) {                        return false;                    }                    @Override                    public boolean containsAll(Collection<?> c) {                        return false;                    }                    @Override                    public boolean addAll(Collection<? extends IMessage> c) {                        return false;                    }                    @Override                    public boolean removeAll(Collection<?> c) {                        return false;                    }                    @Override                    public boolean retainAll(Collection<?> c) {                        return false;                    }                    @Override                    public void clear() {                    }                };                @Override                public String getResourceBundle() {                    return RESOURCE_BUNDLE;                }                @Override                public Queue<IMessage> getIMessages() {                    return messages;                }            };            Registry registryNewAttr = null;            Registry registryChangeAttr = null;            try {                Registry registry = processingRegistry(registryParser, registryLinker, registryHandler, registryBean, finishCallback, messenger, "ree_open_account.txt");                registryBean.delete(registry);                registryNewAttr = processingRegistry(registryParser, registryLinker, registryHandler, registryBean, finishCallback, messenger, "ree_new_attr.txt");                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));                assertEquals(3, eircAccounts.size());                Map<Long, DomainObject> oldObjectAttributes = Maps.newHashMap();                for (EircAccount eircAccount : eircAccounts) {                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));                    assertEquals(1, serviceProviderAccounts.size());                    DomainObject attributes = serviceProviderAccountStrategy.findById(serviceProviderAccounts.get(0).getId(), true);                    long countApplyAttributes = 0;                    oldObjectAttributes.put(attributes.getId(), CloneUtil.cloneObject(attributes));                    for (Attribute attribute : attributes.getAttributes()) {                        if (((ServiceProviderAccountAttribute)attribute).getPkId() != null) {                            countApplyAttributes++;                            assertNull(attribute.getEndDate());                        }                    }                    assertEquals(1, countApplyAttributes);                }                registryChangeAttr = processingRegistry(registryParser, registryLinker, registryHandler, registryBean, finishCallback, messenger, "ree_change_attr.txt");                List<RegistryRecordData> records = registryRecordBean.getRegistryRecords(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryChangeAttr.getId())));                for (EircAccount eircAccount : eircAccounts) {                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));                    assertEquals(1, serviceProviderAccounts.size());                    DomainObject attributes = serviceProviderAccountStrategy.findById(serviceProviderAccounts.get(0).getId(), true);                    long countApplyAttributes = 0;                    DomainObject oldAttributes = oldObjectAttributes.get(attributes.getId());                    for (Attribute attribute : attributes.getAttributes()) {                        if (((ServiceProviderAccountAttribute)attribute).getPkId() != null) {                            countApplyAttributes++;                            assertNull(attribute.getEndDate());                            assertNotEquals(attribute.getStringValue(), oldAttributes.getAttribute(attribute.getAttributeTypeId()).getStringValue());                        }                    }                    assertEquals(1, countApplyAttributes);                }                for (RegistryRecordData record : records) {                    for (Container container1 : record.getContainers()) {                        assertNotNull(serviceProviderAccountAttrChangingBean.findChanging(container1.getId()));                    }                }                for (RegistryRecordData record : records) {                    assertTrue(registryHandler.rollbackRegistryRecord(registry, record));                }                for (RegistryRecordData record : records) {                    for (Container container1 : record.getContainers()) {                        assertNull(serviceProviderAccountAttrChangingBean.findChanging(container1.getId()));                    }                }                for (EircAccount eircAccount : eircAccounts) {                    List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(new ServiceProviderAccount(eircAccount)));                    assertEquals(1, serviceProviderAccounts.size());                    DomainObject attributes = serviceProviderAccountStrategy.findById(serviceProviderAccounts.get(0).getId(), true);                    long countApplyAttributes = 0;                    DomainObject oldAttributes = oldObjectAttributes.get(attributes.getId());                    for (Attribute attribute : attributes.getAttributes()) {                        if (((ServiceProviderAccountAttribute)attribute).getPkId() != null) {                            countApplyAttributes++;                            assertNull(attribute.getEndDate());                            assertEquals(attribute.getStringValue(), oldAttributes.getAttribute(attribute.getAttributeTypeId()).getStringValue());                        }                    }                    assertEquals(1, countApplyAttributes);                }            } finally {                try {                    if (registryNewAttr != null) {                        registryBean.delete(registryNewAttr);                    }                } catch (Throwable th) {                    //                }                try {                    if (registryChangeAttr != null) {                        registryBean.delete(registryChangeAttr);                    }                } catch (Throwable th) {                    //                }                List<EircAccount> eircAccounts = eircAccountBean.getEircAccounts(new FilterWrapper<>(new EircAccount()));                for (EircAccount eircAccount : eircAccounts) {                    try {                        eircAccountBean.archive(eircAccount);                    } catch (Throwable th) {                        //                    }                }            }        }    }    private Registry processingRegistry(RegistryParser registryParser, RegistryLinker registryLinker,                                    RegistryHandler registryHandler, RegistryBean registryBean,                                    AbstractFinishCallback finishCallback, AbstractMessenger messenger, String fileName)            throws IOException, ExecuteException, InterruptedException {        Registry registry = null;        try {            InputStream inputStream = Resources.getResourceAsStream("data/" + fileName);            StopWatch watch = new StopWatch();            watch.start();            registry = registryParser.parse(messenger, finishCallback, inputStream, fileName, 1000, 1000);            while (!finishCallback.isCompleted()) {                Thread.sleep(1000L);            }            watch.stop();            System.out.println("Parse watch time: " + watch.toString());            watch.reset();            assertNotNull("Registry did not create", registry);            assertEquals("Registry parsed with errors", RegistryStatus.LOADED, registry.getStatus());            watch.start();            registryLinker.link(registry.getId(), messenger, finishCallback);            while (!finishCallback.isCompleted()) {                Thread.sleep(1000L);            }            watch.stop();            System.out.println("Link watch time: " + watch.toString());            watch.reset();            List<Registry> registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registry.getId())));            assertEquals("Registry linked with errors", RegistryStatus.LINKED, registries.get(0).getStatus());            watch.start();            registryHandler.handle(registry.getId(), messenger, finishCallback);            while (!finishCallback.isCompleted()) {                Thread.sleep(1000L);            }            watch.stop();            System.out.println("Handle watch time: " + watch.toString());            watch.reset();            registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registry.getId())));            assertEquals("Registry handle with errors", RegistryStatus.PROCESSED, registries.get(0).getStatus());        } catch (Throwable th) {            if (registry != null) {                registryBean.delete(registry);            }            throw th;        }        return registry;    }}