package ru.complitex.common.web.component.domain;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Status;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;

/**
 *
 * @author Artem
 */
public final class DomainObjectAccessUtil {

    private DomainObjectAccessUtil() {
    }

    private static IStrategy getStrategy(String strategyName, String entity) {
        StrategyFactory strategyFactory = EjbBeanLocator.getBean(StrategyFactory.class);
        return strategyFactory.getStrategy(strategyName, entity);
    }

    private static String[] getEditRoles(String strategyName, String entity) {
        return getStrategy(strategyName, entity).getEditRoles();
    }

    private static boolean isNew(DomainObject object) {
        return object.getObjectId() == null;
    }

    public static boolean canAddNew(String strategyName, String entity) {
        return getApplication().hasAnyRole(new Roles(getEditRoles(strategyName, entity)));
    }

    public static boolean canAddNew(IStrategy strategy, String entity) {
        return getApplication().hasAnyRole(new Roles(strategy.getEditRoles()));
    }

    public static boolean canEdit(String strategyName, String entity, DomainObject object) {
        return (isNew(object) || object.getStatus() == Status.ACTIVE)
                && getApplication().hasAnyRole(new Roles(getEditRoles(strategyName, entity)));
    }

    public static boolean canDisable(String strategyName, String entity, DomainObject object) {
        return !isNew(object) && (object.getStatus() == Status.ACTIVE)
                && getApplication().hasAnyRole(new Roles(getEditRoles(strategyName, entity)));
    }

    public static boolean canEnable(String strategyName, String entity, DomainObject object){
        return !isNew(object) && (object.getStatus() == Status.INACTIVE)
                && getApplication().hasAnyRole(new Roles(getEditRoles(strategyName, entity)));
    }

    private static IRoleCheckingStrategy getApplication() {
        return (IRoleCheckingStrategy) Application.get();
    }

    public static boolean canDelete(String strategyName, String entity, DomainObject object){
        return !isNew(object) && getApplication().hasAnyRole(new Roles(getEditRoles(strategyName, entity)));
    }
}
