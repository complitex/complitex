package org.complitex.common.service.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.concurrent.Future;

/**
 * @author inheaven on 19.05.2016.
 */
@Stateless
public class AsyncBean {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Asynchronous
    public Future<String> async(Runnable runnable){
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("error async", e);
        }

        return new AsyncResult<>("async");
    }
}
