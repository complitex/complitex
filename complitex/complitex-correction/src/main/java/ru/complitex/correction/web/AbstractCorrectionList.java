package ru.complitex.correction.web;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.datatable.ArrowOrderByBorder;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.organization.OrganizationIdPicker;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.component.scroll.ScrollBookmarkablePageLink;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.entity.CorrectionOrderBy;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.organization_type.strategy.OrganizationTypeStrategy;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;

/**
 * Абстрактный класс для списка коррекций.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public abstract class AbstractCorrectionList extends TemplatePage {
    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private CorrectionBean correctionBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private IOrganizationStrategy organizationStrategy;

    private String entityName;
    private FilterWrapper<Correction> filterWrapper;

    public AbstractCorrectionList(String entityName) {
        this.entityName = entityName;
        setPreferencesPage(getClass().getName() + "#" + entityName);

        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AbstractCorrectionList.class,
                AbstractCorrectionList.class.getSimpleName() + ".css")));
    }

    protected String getEntityName() {
        return entityName;
    }

    protected void clearFilter() {
        filterWrapper.setObject(newCorrection());
    }

    protected Correction newCorrection(){
        return new Correction(entityName, null);
    }

    protected List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper){
        return correctionBean.getCorrections(filterWrapper);
    }

    protected Long getCorrectionsCount(FilterWrapper<Correction> filterWrapper){
        return correctionBean.getCorrectionsCount(filterWrapper);
    }

    protected String displayCorrection(Correction correction) {
        return correction.getCorrection();
    }

    protected String displayInternalObject(Correction correction) {
        if (correction.getDisplayObject() == null){
            IStrategy strategy = strategyFactory.getStrategy(entityName);
            DomainObject object = strategy.getDomainObject(correction.getObjectId(), false);

            if (object == null) { //объект доступен только для просмотра
                object = strategy.getDomainObject(correction.getObjectId(), true);
                correction.setEditable(false);
            }

            correction.setDisplayObject(strategy.displayDomainObject(object, getLocale()));
        }

        return correction.getDisplayObject();
    }

    protected abstract Class<? extends WebPage> getEditPage();

    protected abstract PageParameters getEditPageParams(Long objectCorrectionId);

    protected IModel<String> getTitleModel(){
        return new ResourceModel("title");
    }

    protected void init() {
        IModel<String> titleModel = getTitleModel();
        add(new Label("title", titleModel));
        add(new Label("label", titleModel));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        add(content);

        FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        content.add(feedbackPanel);

        final Form filterForm = new Form("filterForm");
        content.add(filterForm);

        filterWrapper = FilterWrapper.of(getFilterObject(newCorrection()));

        final DataProvider<Correction> dataProvider = new DataProvider<Correction>() {

            @Override
            protected Iterable<Correction> getData(long first, long count) {
                //store preference, but before clear data order related properties.
                {
                    filterWrapper.setAscending(false);
                    filterWrapper.setSortProperty(null);
                    setFilterObject(filterWrapper.getObject());
                }

                filterWrapper.setAscending(getSort().isAscending());
                if (!Strings.isEmpty(getSort().getProperty())) {
                    filterWrapper.setSortProperty(getSort().getProperty());
                }
                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);

                return getCorrections(filterWrapper);
            }

            @Override
            protected Long getSize() {
                long limitCount = filterWrapper.getCount();
                return getCorrectionsCount(filterWrapper);
            }
        };
        dataProvider.setSort("", SortOrder.ASCENDING);


        filterForm.add(new OrganizationIdPicker("object.organizationId",
                new PropertyModel<>(filterWrapper, "object.organizationId"),
                getOrganizationTypeIds()));

        filterForm.add(new OrganizationIdPicker("object.userOrganizationId",
                new PropertyModel<>(filterWrapper, "object.userOrganizationId"),
                OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));

        filterForm.add(new TextField<>("correctionFilter", new PropertyModel<>(filterWrapper, "object.correction")));
        filterForm.add(new TextField<>("codeFilter", new PropertyModel<>(filterWrapper, "object.externalId")));
        filterForm.add(new TextField<>("internalObjectIdFilter", new PropertyModel<>(filterWrapper, "object.objectId")));
        filterForm.add(new TextField<>("internalObjectFilter", new PropertyModel<>(filterWrapper, "object.internalObject")));

        AjaxLink reset = new IndicatingAjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                clearFilter();
                target.add(content);
            }
        };
        filterForm.add(reset);
        AjaxButton submit = new IndicatingAjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(content);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(submit);

        DataView<Correction> data = new DataView<Correction>("data", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Correction> item) {
                final Correction correction = item.getModelObject();

                item.add(new Label("organization", organizationStrategy
                        .displayShortNameAndCode(correction.getOrganizationId(), getLocale())));
                item.add(new Label("correction", displayCorrection(correction)));

                item.add(new Label("code", StringUtil.emptyOnNull(correction.getExternalId())));

                item.add(new Label("internalObjectId", correction.getObjectId()));
                item.add(new Label("internalObject", displayInternalObject(correction)));

                //user organization
                item.add(new Label("userOrganization", organizationStrategy
                        .displayShortNameAndCode(correction.getUserOrganizationId(), getLocale())));

                ScrollBookmarkablePageLink link = new ScrollBookmarkablePageLink<WebPage>("edit", getEditPage(),
                        getEditPageParams(correction.getId()), String.valueOf(correction.getId()));
                link.setVisible(correction.isEditable());

                item.add(new Link("delete") {
                    @Override
                    public void onClick() {
                        onDelete(correction);

                        getSession().info(getStringFormat("info_deleted", correction.getCorrection()));
                    }
                }.setVisible(isDeleteVisible()));

                item.add(link);
            }
        };
        filterForm.add(data);

        filterForm.add(new ArrowOrderByBorder("organizationHeader", CorrectionOrderBy.ORGANIZATION.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("correctionHeader", CorrectionOrderBy.CORRECTION.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("codeHeader", CorrectionOrderBy.EXTERNAL_ID.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("internalObjectIdHeader", CorrectionOrderBy.OBJECT.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("internalObjectHeader", CorrectionOrderBy.OBJECT.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("userOrganizationHeader", CorrectionOrderBy.USER_ORGANIZATION.getOrderBy(), dataProvider, data, content));

        content.add(new PagingNavigator("navigator", data, getPreferencesPage() + "#" + entityName, content));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(getEditPage(), getEditPageParams(null));
            }
        });
    }

    protected void onDelete(Correction correction){
    }

    protected boolean isDeleteVisible(){
        return false;
    }

    protected Long[] getOrganizationTypeIds(){
        return null;
    }
}
