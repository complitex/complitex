package ru.flexpay.eirc;

import org.complitex.common.service.AbstractBean;
import org.complitex.common.test.TestBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless
public class EircContainerTestBean extends AbstractBean {

    @EJB
    private TestBean testBean;


    public void testTransactional() {
        testBean.testSelectSimple("");
    }


    public void testTransactionalWithSleep(long time) {
        testBean.testSelectSimpleWithSleep("", time);
        //testBean.testSelectSimple("");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            //
        }
    }

}
