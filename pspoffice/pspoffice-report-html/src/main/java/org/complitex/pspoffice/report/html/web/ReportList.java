package org.complitex.pspoffice.report.html.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.pspoffice.report.html.entity.Report;
import org.complitex.pspoffice.report.html.service.ReportBean;
import org.complitex.template.web.component.toolbar.AddDocumentButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 18:35
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ReportList extends TemplatePage {
    private final Logger log = LoggerFactory.getLogger(ReportList.class);

    @EJB
    private ReportBean reportBean;

    public ReportList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final FilterWrapper<Report> filterWrapper = FilterWrapper.of(new Report());

        final Form form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                form.clearInput();
                filterWrapper.setObject(new Report());
            }
        };
        form.add(filter_reset);

        //Фильтр
        form.add(new TextField<>("name", new PropertyModel<>(filterWrapper, "object.name")));
        form.add(new DatePicker<>("updated", new PropertyModel<Date>(filterWrapper, "object.updated")));

        DataProvider<Report> dataProvider = new DataProvider<Report>() {
            @Override
            protected Iterable<? extends Report> getData(long first, long count) {
                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return reportBean.getReportList(filterWrapper);
            }

            @Override
            protected int getSize() {
                return reportBean.getReportListCount(filterWrapper);
            }
        };
        dataProvider.setSort("updated", SortOrder.DESCENDING);

        DataView dataView = new DataView<Report>("reports", dataProvider) {
            @Override
            protected void populateItem(Item<Report> item) {
                Report report = item.getModelObject();

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", report.getId());

                BookmarkablePageLink link = new BookmarkablePageLink<Void>("link", ReportEdit.class, pageParameters);
                item.add(link);

                link.add(new Label("name", report.getName()));

                item.add(DateLabel.forDatePattern("updated", Model.of(report.getUpdated()), "dd.MM.yy HH:mm:ss"));

                item.add(new BookmarkablePageLinkPanel<>("view", getString("view"), ReportView.class, report.getId()));
                item.add(new BookmarkablePageLinkPanel<>("html", getString("html"), ReportHtml.class, report.getId()));
                item.add(new ReportPdfLink("pdf", report.getId()));
            }
        };
        form.add(dataView);

        //Сортировка
        form.add(new OrderByBorder("header.name", "name", dataProvider));
        form.add(new OrderByBorder("header.updated", "updated", dataProvider));

        //Постраничная навигация
        form.add(new PagingNavigator("paging", dataView, getClass().getName(), form));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(ReportEdit.class);
            }
        });
    }
}
