package org.complitex.common.web.component;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.StringLocaleBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Iterables.find;

/**
 *
 * @author Artem
 */
public class EntityTypePanel extends Panel {

    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private StringLocaleBean stringLocaleBean;
    @EJB
    private StringCultureBean stringBean;
    private String entityType;
    private long entityTypeDisplayAttributeTypeId;
    private IModel<Long> entityTypeObjectIdModel;
    private IModel<String> labelModel;
    private boolean enabled;
    private boolean required;

    public EntityTypePanel(String id, String entityType, long entityTypeDisplayAttributeTypeId,
            IModel<Long> entityTypeObjectIdModel, IModel<String> labelModel, boolean required, boolean enabled) {
        super(id);

        this.entityType = entityType;
        this.entityTypeDisplayAttributeTypeId = entityTypeDisplayAttributeTypeId;
        this.entityTypeObjectIdModel = entityTypeObjectIdModel;
        this.labelModel = labelModel;
        this.enabled = enabled;
        this.required = required;

        init();
    }

    private String displayEntityTypeObject(DomainObject entityTypeObject, Locale locale) {
        return entityTypeObject.getAttribute(entityTypeDisplayAttributeTypeId).getStringValue(locale);
    }

    private void init() {
        final IModel<List<? extends DomainObject>> entityTypesModel = new LoadableDetachableModel<List<? extends DomainObject>>() {

            @Override
            protected List<? extends DomainObject> load() {
                return getEntityTypes();
            }
        };
        IModel<DomainObject> entityTypeModel = new Model<DomainObject>() {

            @Override
            public DomainObject getObject() {
                final Long entityTypeObjectId = entityTypeObjectIdModel.getObject();
                if (entityTypeObjectId != null) {
                    return find(entityTypesModel.getObject(), new Predicate<DomainObject>() {

                        @Override
                        public boolean apply(DomainObject entityTypeId) {
                            return entityTypeId.getObjectId().equals(entityTypeObjectId);
                        }
                    });
                }
                return null;
            }

            @Override
            public void setObject(DomainObject entityTypeObject) {
                entityTypeObjectIdModel.setObject(entityTypeObject.getObjectId());
            }
        };
        DomainObjectDisableAwareRenderer renderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject entityTypeObject) {
                return displayEntityTypeObject(entityTypeObject, getLocale());
            }
        };
        DisableAwareDropDownChoice<DomainObject> entityTypeChoice = new DisableAwareDropDownChoice<DomainObject>("entityType",
                entityTypeModel, entityTypesModel, renderer);
        entityTypeChoice.setRequired(required);
        entityTypeChoice.setEnabled(enabled);
        entityTypeChoice.setLabel(labelModel);
        add(entityTypeChoice);
    }

    private List<? extends DomainObject> getEntityTypes() {
        IStrategy strategy = strategyFactory.getStrategy(entityType);
        DomainObjectFilter example = new DomainObjectFilter();
        example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
        example.setOrderByAttributeTypeId(entityTypeDisplayAttributeTypeId);
        example.setAsc(true);
        strategy.configureExample(example, ImmutableMap.<String, Long>of(), null);
        return strategy.getList(example);
    }
}
