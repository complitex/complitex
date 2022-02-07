package ru.complitex.keconnection.heatmeter.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.web.component.EnumDropDownChoice;
import ru.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.keconnection.heatmeter.entity.Tablegram;
import ru.complitex.keconnection.heatmeter.entity.TablegramRecord;
import ru.complitex.keconnection.heatmeter.entity.TablegramRecordStatus;
import ru.complitex.keconnection.heatmeter.service.TablegramBean;
import ru.complitex.keconnection.heatmeter.service.TablegramRecordBean;
import ru.complitex.keconnection.heatmeter.service.TablegramService;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.HashMap;
import java.util.Map;

import static ru.complitex.common.util.DateUtil.getFirstDayOfMonth;
import static ru.complitex.common.util.PageUtil.*;
import static ru.complitex.keconnection.heatmeter.entity.TablegramRecordStatus.PROCESSED;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.10.12 17:14
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TablegramRecordList extends TemplatePage{

    private final Logger log = LoggerFactory.getLogger(TablegramRecordList.class);

    @EJB
    private TablegramRecordBean tablegramRecordBean;

    @EJB
    private TablegramService tablegramService;

    @EJB
    private TablegramBean tablegramBean;

    //properties
    private final String[] properties = new String[]{
            "heatmeterId", "ls", "name", "address", "payload1", "payload2", "payload3", "status"
    };

    public TablegramRecordList(PageParameters pageParameters) {
        Long tablegramId = pageParameters.get("t_id").toLongObject();

        final Tablegram tablegram = tablegramBean.getTablegram(tablegramId);

        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        FilterWrapper<TablegramRecord> filterWrapper = getTemplateSession()
                .getPreferenceFilter(TablegramRecordList.class.getName() + tablegramId,
                        FilterWrapper.of(new TablegramRecord(tablegramId)));
        final IModel<FilterWrapper<TablegramRecord>> filterModel = new CompoundPropertyModel<>(filterWrapper);

        //Filter Form
        final Form filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxLink filterReset = new AjaxLink("filter_reset") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                filterModel.setObject(FilterWrapper.of(new TablegramRecord()));
                target.add(filterForm);
            }
        };
        filterForm.add(filterReset);

        //Filter Find
        AjaxButton filterFind = new AjaxButton("filter_find") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(filterForm);
                target.add(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        filterForm.add(filterFind);

        //Filter Fields
        filterForm.add(newTextFields("object.", properties));
        filterForm.replace(new EnumDropDownChoice<>("object.status", TablegramRecordStatus.class, true));

        //Selected Heatmeaters Id Map
        final Map<String, Long> selectedIds = new HashMap<>();

        //Data Provider
        DataProvider<TablegramRecord> dataProvider = new DataProvider<TablegramRecord>() {
            @Override
            protected Iterable<TablegramRecord> getData(long first, long count) {
                FilterWrapper<TablegramRecord> filterWrapper = filterModel.getObject();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return tablegramRecordBean.getTablegramRecords(filterWrapper);
            }

            @Override
            protected Long getSize() {
                return tablegramRecordBean.getTablegramRecordsCount(filterModel.getObject());
            }

            @Override
            public IModel<TablegramRecord> model(TablegramRecord object) {
                return new CompoundPropertyModel<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Data Container
        final WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        //Data View
        DataView dataView = new DataView<TablegramRecord>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<TablegramRecord> item) {
                final TablegramRecord tablegramRecord = item.getModelObject();

                item.add(newTextLabels(properties));

                item.add(new Link("process") {
                    @Override
                    public void onClick() {
                        try {
                            tablegramService.process(tablegramRecord, null, getFirstDayOfMonth(tablegram.getBeginDate()),
                                    tablegram.getBeginDate());

                            info(getStringFormat("info_processed", tablegramRecord.getLs(), tablegramRecord.getAddress())
                                    + ": " + getString(tablegramRecord.getStatus().name()));
                        } catch (Exception e) {
                            error(e.getMessage());
                        }
                    }

                    @Override
                    public boolean isVisible() {
                        return !PROCESSED.equals(tablegramRecord.getStatus());
                    }
                });
            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        final PagingNavigator paging = new PagingNavigator("paging", dataView, TablegramRecordList.class.getName(), filterForm);
        filterForm.add(paging);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, true, properties));
    }
}
