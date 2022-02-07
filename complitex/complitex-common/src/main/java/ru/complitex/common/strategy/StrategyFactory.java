package ru.complitex.common.strategy;

import ru.complitex.common.util.EjbBeanLocator;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import static org.apache.wicket.util.string.Strings.isEmpty;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class StrategyFactory {

    public IStrategy getStrategy(String entityName) {
        return getStrategy(entityName, false);
    }

    public IStrategy getStrategy(String entityName, boolean suppressException) {
        String strategyName = getStrategyName(entityName);

        return EjbBeanLocator.getBean(strategyName, suppressException);
    }

    public IStrategy getStrategy(String strategyName, String entityName) {
        if (isEmpty(strategyName)) {
            return getStrategy(entityName);
        }

        IStrategy strategy = EjbBeanLocator.getBean(strategyName);

        if (!strategy.getEntityName().equals(entityName)) {
            throw new IllegalArgumentException("Strategy with class " + strategy.getClass() + " has entity table "
                    + strategy.getEntityName() + " that one is not equal to requested entity table - " + entityName);
        }
        return strategy;
    }

    private String getStrategyName(String entityName) {
        if (entityName == null || isEmpty(entityName)) {
            throw new IllegalStateException("Entity table is null or empty.");
        }
        char[] chars = entityName.toCharArray();
        StringBuilder strategyNameBuilder = new StringBuilder();
        strategyNameBuilder.append(Character.toUpperCase(chars[0]));
        int i = 1;
        while (true) {
            if (i >= chars.length) {
                break;
            }
            char c = chars[i];
            if (c == '_') {
                if (++i < chars.length) {
                    strategyNameBuilder.append(Character.toUpperCase(chars[i]));
                }
            } else {
                strategyNameBuilder.append(c);
            }
            i++;
        }
        strategyNameBuilder.append("Strategy");
        return strategyNameBuilder.toString();
    }
}
