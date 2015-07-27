package org.complitex.keconnection.web;

import org.complitex.common.service.BroadcastService;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.keconnection.heatmeter.web.consumption.CentralHeatingConsumptionFileList;
import org.complitex.template.web.ComplitexWebApplication;

/**
 * @author inheaven on 007 07.04.15 19:08
 */
public class KeWebApplication extends ComplitexWebApplication{
    @Override
    protected void init() {
        super.init();

//        try {
//            BeanManager beanManager = (BeanManager)new InitialContext().lookup("java:comp/BeanManager");
//
//            new CdiConfiguration(beanManager).configure(this);
//
//            Bean bean = beanManager.getBeans(BroadcastService.class).iterator().next();
//            CreationalContext ctx = beanManager.createCreationalContext(bean);
//            BroadcastService broadcastService = (BroadcastService) beanManager.getReference(bean, BroadcastService.class, ctx);
//            broadcastService.setApplication(this);
//        } catch (NamingException | NullPointerException e) {
//            e.printStackTrace();
//        }


        //broadcaster
        EjbBeanLocator.getBean(BroadcastService.class).setApplication(this);

        mountPage("/central-heating-consumptions", CentralHeatingConsumptionFileList.class);
    }
}
