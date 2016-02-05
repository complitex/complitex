package org.complitex.common.web.component;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.StatusType;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.web.component.domain.DomainObjectAccessUtil;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
public final class Children extends Panel {

    @EJB
    private StrategyFactory strategyFactory;

    private String childEntity;
    private String childStrategyName;
    private String parentEntity;
    private DomainObject parentObject;

    public Children(String id, String parentEntity, DomainObject parentObject,
                    String childStrategyName, String childEntity) {
        super(id);
        this.childEntity = childEntity;
        this.parentEntity = parentEntity;
        this.parentObject = parentObject;
        this.childStrategyName = childStrategyName;
        init();
    }

    private IStrategy getChildrenStrategy() {
        return strategyFactory.getStrategy(childEntity);
    }

    private class ToggleModel extends AbstractReadOnlyModel<String> {

        private boolean expanded;

        @Override
        public String getObject() {
            return expanded ? getString("hide") : getString("show");
        }

        public void toggle() {
            expanded = !expanded;
        }

        public boolean isExpanded() {
            return expanded;
        }
    }

    private void init() {
        Label title = new Label("title", strategyFactory.getStrategy(childEntity).getPluralEntityLabel(getLocale()));
        add(title);

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        content.setVisible(false);
        add(content);

        final ToggleModel toggleModel = new ToggleModel();
        final Label toggleStatus = new Label("toggleStatus", toggleModel);
        toggleStatus.setOutputMarkupId(true);
        IndicatingAjaxLink<Void> toggleLink = new IndicatingAjaxLink<Void>("toggleLink") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (toggleModel.isExpanded()) {
                    content.setVisible(false);
                } else {
                    content.setVisible(true);
                }
                toggleModel.toggle();
                target.add(toggleStatus);
                target.add(content);
            }
        };
        toggleLink.add(toggleStatus);
        add(toggleLink);

        IModel<List<DomainObject>> childrenModel = new AbstractReadOnlyModel<List<DomainObject>>() {

            private List<DomainObject> children;

            @Override
            public List<DomainObject> getObject() {
                if (children == null) {
                    initChildren();
                }
                return children;
            }

            private void initChildren() {
                DomainObjectFilter example = new DomainObjectFilter();
                if (StatusType.ACTIVE.equals(parentObject.getStatus())) {
                    example.setStatus(ShowMode.ACTIVE.name());
                } else {
                    example.setStatus(ShowMode.ALL.name());
                }
                getChildrenStrategy().configureFilter(example, ImmutableMap.of(parentEntity, parentObject.getObjectId()), null);
                children = (List<DomainObject>) getChildrenStrategy().getList(example);
            }
        };

        ListView<DomainObject> children = new ListView<DomainObject>("children", childrenModel) {

            @Override
            protected void populateItem(ListItem<DomainObject> item) {
                DomainObject child = item.getModelObject();
                BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("link", getChildrenStrategy().getEditPage(),
                        getChildrenStrategy().getEditPageParams(child.getObjectId(), parentObject.getObjectId(), parentEntity));
                link.add(new Label("displayName", getChildrenStrategy().displayDomainObject(child, getLocale())));
                item.add(link);
            }
        };
        children.setReuseItems(true);
        content.add(children);
        BookmarkablePageLink addLink = new BookmarkablePageLink("add", getChildrenStrategy().getEditPage(),
                getChildrenStrategy().getEditPageParams(null, parentObject.getObjectId(), parentEntity));
        content.add(addLink);
        if (!DomainObjectAccessUtil.canEdit(childStrategyName, parentEntity, parentObject)) {
            addLink.setVisible(false);
        }
    }
}
