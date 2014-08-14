/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.address.street;

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Method;
import java.util.Set;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.complitex.address.strategy.street.StreetStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Street strategy interceptor. 
 * <p>Intercepts calls to "found" and "count" street strategy operations and delegates to {@link  StreetStrategyDelegate}.
 * Interceptor is declared in WEB-INF/ejb-jar.xml</p>
 * 
 * @see StreetStrategy
 * @see StreetStrategyDelegate
 * 
 * @author Artem
 */
public class StreetStrategyInterceptor {

    private final Logger log = LoggerFactory.getLogger(StreetStrategyInterceptor.class);
    private final static Set<String> DELEGATE_METHODS = ImmutableSet.of("find", "count");
    @EJB
    private StreetStrategyDelegate streetStrategyDelegate;

    @AroundInvoke
    public Object delegate(InvocationContext invocationContext) throws Exception {
        final Method originalMethod = invocationContext.getMethod();

        final String debugPrefix = log.isDebugEnabled() ? "[Intercepted method: " + originalMethod.toGenericString() + "]" : null;
        if (log.isDebugEnabled()) {
            log.debug(debugPrefix + " - StreetStrategyInterceptor started.");
        }

        if (!DELEGATE_METHODS.contains(originalMethod.getName())) {
            if (log.isDebugEnabled()) {
                log.debug(debugPrefix + " - Proceed at original method.");
            }

            return invocationContext.proceed();
        }

        if (log.isDebugEnabled()) {
            log.debug(debugPrefix + " - Delegate to StreetStrategyDelegate.");
        }

        final Method delegateMethod = StreetStrategyDelegate.class.getMethod(originalMethod.getName(),
                originalMethod.getParameterTypes());
        return delegateMethod.invoke(streetStrategyDelegate, invocationContext.getParameters());
    }
}
