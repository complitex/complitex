package ru.complitex.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import ru.complitex.common.entity.*;
import ru.complitex.common.entity.*;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;
import ru.complitex.common.web.component.search.CollapsibleInputSearchComponent;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.common.web.component.search.SearchComponentState;
import org.slf4j.LoggerFactory;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DomainObjectInputPanel extends Panel {

    @EJB
    private StrategyFactory strategyFactory;
    private SearchComponentState searchComponentState;
    private String entity;
    private String strategyName;
    private DomainObject object;
    private Long parentId;
    private String parentEntity;
    private Date date;
    private final Entity description;

    /**
     * For use in history components
     * @param id
     * @param object
     * @param entity
     * @param parentId
     * @param parentEntity
     * @param date
     */
    public DomainObjectInputPanel(String id, DomainObject object, String entity, String strategyName, Long parentId,
            String parentEntity, Date date) {
        super(id);
        this.object = object;
        this.entity = entity;
        this.strategyName = strategyName;
        this.parentId = parentId;
        this.parentEntity = parentEntity;
        this.date = date;
        this.description = getStrategy().getEntity();
        init();
    }

    /**
     * For use in non-history components
     * @param id
     * @param object
     * @param entity
     * @param parentId
     * @param parentEntity
     */
    public DomainObjectInputPanel(String id, DomainObject object, String entity, String strategyName, Long parentId,
            String parentEntity) {
        super(id);
        this.object = object;
        this.entity = entity;
        this.strategyName = strategyName;
        this.parentId = parentId;
        this.parentEntity = parentEntity;
        this.description = getStrategy().getEntity();
        init();
    }

    public Date getDate() {
        return date;
    }

    private boolean isHistory() {
        return date != null;
    }

    public String getParentEntity() {
        return parentEntity;
    }

    public Long getParentId() {
        return parentId;
    }

    private IStrategy getStrategy() {
        return strategyFactory.getStrategy(strategyName, entity);
    }

    public DomainObject getObject() {
        return object;
    }

    private void init() {
        add(new WebMarkupContainer("objectId")
                .add(new Label("label", Model.of(object.getObjectId())))
                .setVisible(object.getObjectId() != null));

        //simple attributes
        ListView<Attribute> simpleAttributes = newSimpleAttributeListView("simpleAttributes");
        simpleAttributes.setReuseItems(true);
        add(simpleAttributes);

        searchComponentState = initParentSearchComponentState();
        WebMarkupContainer parentContainer = new WebMarkupContainer("parentContainer");
        add(parentContainer);
        List<String> parentFilters = getStrategy().getParentSearchFilters();
        ISearchCallback parentSearchCallback = getStrategy().getParentSearchCallback();
        if (parentFilters == null || parentFilters.isEmpty() || parentSearchCallback == null) {
            parentContainer.setVisible(false);
            parentContainer.add(new EmptyPanel("parentSearch"));
        } else {
            CollapsibleInputSearchComponent parentSearchComponent = new CollapsibleInputSearchComponent("parentSearch", getParentSearchComponentState(),
                    parentFilters, parentSearchCallback, ShowMode.ACTIVE, !isHistory() && DomainObjectAccessUtil.canEdit(strategyName, entity, object)) {

                @Override
                protected void onSelect(AjaxRequestTarget target, String entity) {
                    super.onSelect(target, entity);

                    if (object.getObjectId() == null) {
                        DomainObject parent = getModelObject(entity);
                        if (parent != null && parent.getObjectId() != null && parent.getObjectId() > 0) {
                            DomainObjectEditPanel editPanel = visitParents(DomainObjectEditPanel.class,
                                    new IVisitor<DomainObjectEditPanel, DomainObjectEditPanel>() {

                                        @Override
                                        public void component(DomainObjectEditPanel object, IVisit<DomainObjectEditPanel> visit) {
                                            visit.stop(object);
                                        }
                                    });
                            editPanel.updateParentPermissions(target, parent.getSubjectIds());
                        }
                    }
                }
            };
            parentContainer.add(parentSearchComponent);
            parentSearchComponent.invokeCallback();
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        //before simple attributes:
        addComplexAttributesPanelBefore("complexAttributesBefore");

        //after simple attributes:
        addComplexAttributesPanelAfter("complexAttributesAfter");
    }

    protected void addComplexAttributesPanelBefore(String id) {
        addComplexAttributesPanel(id, getStrategy().getComplexAttributesPanelBeforeClass());
    }

    protected void addComplexAttributesPanelAfter(String id) {
        addComplexAttributesPanel(id, getStrategy().getComplexAttributesPanelAfterClass());
    }

    protected ListView<Attribute> newSimpleAttributeListView(String id) {
        return new ListView<Attribute>(id, getSimpleAttributes(object.getAttributes())) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                Attribute attr = item.getModelObject();

                EntityAttribute entityAttribute = description.getAttribute(attr.getEntityAttributeId());

                item.add(new Label("label", DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
                WebMarkupContainer required = new WebMarkupContainer("required");
                item.add(required);
                required.setVisible(entityAttribute.isRequired());

                item.add(DomainObjectComponentUtil.newInputComponent(entity, strategyName, object, attr,
                        getLocale(), isHistory()));
            }
        };
    }

    protected List<Attribute> getSimpleAttributes(List<Attribute> allAttributes) {
        List<Attribute> attributes = new ArrayList<>();

        for (Attribute attribute : allAttributes) {
            EntityAttribute entityAttribute = description.getAttribute(attribute.getEntityAttributeId());

            if (getStrategy().isSimpleAttributeType(entityAttribute)) {
                attributes.add(attribute);
            }
        }

        attributes.sort(Comparator.comparing(a -> description.getAttribute(a.getEntityAttributeId()).getId()));

        return attributes;
    }

    protected void addComplexAttributesPanel(String id, Class<? extends AbstractComplexAttributesPanel> complexAttributesPanelClass) {
        AbstractComplexAttributesPanel complexAttributes = null;
        if (complexAttributesPanelClass != null) {
            try {
                complexAttributes = complexAttributesPanelClass.getConstructor(String.class, boolean.class).
                        newInstance(id, isHistory());
            } catch (Exception e) {
                LoggerFactory.getLogger(getClass()).error("Couldn't instantiate complex attributes panel object.", e);
            }
        }
        if (complexAttributes == null) {
            add(new EmptyPanel(id));
        } else {
            add(complexAttributes);
        }
    }

    protected SearchComponentState initParentSearchComponentState() {
        //parent search
        SearchComponentState componentState = null;
        if (object.getObjectId() == null) {
            if (parentId != null && !Strings.isEmpty(parentEntity)) {
                if (parentId > 0) {
                    componentState = getStrategy().getSearchComponentStateForParent(parentId, parentEntity, null);
                }
            }
        } else {
            EntityObjectInfo info = getStrategy().findParentInSearchComponent(object.getObjectId(), isHistory() ? date : null);
            if (info != null) {
                componentState = getStrategy().getSearchComponentStateForParent(info.getId(), info.getEntityName(), date);
            }
        }

        if (componentState == null) {
            componentState = new SearchComponentState();
        }

        return componentState;
    }

    public boolean validateParent() {
        if (!(getStrategy().getParentSearchFilters() == null
                || getStrategy().getParentSearchFilters().isEmpty()
                || getStrategy().getParentSearchCallback() == null)) {
            if ((object.getParentId() == null) || (object.getParentEntityId() == null)) {
                error(getString("parent_required"));
                return false;
            }
        }
        return true;
    }

    public SearchComponentState getParentSearchComponentState() {
        return searchComponentState;
    }
}
