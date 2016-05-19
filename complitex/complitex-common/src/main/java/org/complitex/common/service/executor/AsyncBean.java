package org.complitex.common.service.executor;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

/**
 * @author inheaven on 19.05.2016.
 */
@Stateless
public class AsyncBean {
    @Asynchronous
    public void async(Runnable runnable){
        runnable.run();
    }
}
