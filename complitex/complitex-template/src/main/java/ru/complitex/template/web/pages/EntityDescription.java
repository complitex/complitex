package ru.complitex.template.web.pages;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.component.domain.EntityDescriptionPanel;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class EntityDescription extends TemplatePage {

    public static final String ENTITY = "entity";
    @EJB
    private StrategyFactory strategyFactory;

    public EntityDescription(PageParameters params) {
        String entity = params.get(ENTITY).toString();
        authorize(entity);
        add(newDescriptionPanel("entityDescriptionPanel", entity));
    }

    protected void authorize(String entity) throws UnauthorizedInstantiationException {
        IStrategy strategy = strategyFactory.getStrategy(entity);
        String[] descriptionRoles = strategy.getDescriptionRoles();
        if (descriptionRoles != null && hasAnyRole(descriptionRoles)) {
            return;
        }
        throw new UnauthorizedInstantiationException(EntityDescription.class);
    }

    protected EntityDescriptionPanel newDescriptionPanel(String id, String entity) {
        return new EntityDescriptionPanel(id, entity, new PageParameters().set(ENTITY, entity));
    }
}
