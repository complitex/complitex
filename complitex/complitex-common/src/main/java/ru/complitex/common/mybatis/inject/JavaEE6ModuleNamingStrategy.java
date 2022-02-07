package ru.complitex.common.mybatis.inject;

import org.wicketstuff.javaee.naming.IJndiNamingStrategy;

/**
 *
 * @author Artem
 */
public class JavaEE6ModuleNamingStrategy implements IJndiNamingStrategy {

    @Override
    public String calculateName(String ejbName, Class<?> ejbType) {
        return "java:module/" + (ejbName == null ? ejbType.getSimpleName() : ejbName);
    }
}
