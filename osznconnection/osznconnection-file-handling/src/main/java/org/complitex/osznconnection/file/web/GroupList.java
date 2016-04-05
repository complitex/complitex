package org.complitex.osznconnection.file.web;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.Log;
import org.complitex.common.service.LogBean;
import org.complitex.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.scroll.ScrollListBehavior;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import org.complitex.osznconnection.file.entity.subsidy.RequestFileGroupFilter;
import org.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;
import org.complitex.osznconnection.file.web.component.load.DateParameter;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel.MonthParameterViewMode;
import org.complitex.osznconnection.file.web.pages.benefit.BenefitList;
import org.complitex.osznconnection.file.web.pages.payment.PaymentList;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.ScrollListPage;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class GroupList extends ScrollListPage {

    @EJB
    private RequestFileGroupBean requestFileGroupBean;
    @EJB
    private ProcessManagerBean processManagerBean;
    @EJB
    private LogBean logBean;
    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;

    private class GroupListPanel extends AbstractProcessableListPanel<RequestFileGroup, RequestFileGroupFilter> {

        GroupListPanel(String id) {
            super(id, LOAD_GROUP, BIND_GROUP, FILL_GROUP, SAVE_GROUP);

            add(new Label("title", new ResourceModel("title")));

            //Файл начислений
            addColumn(new Column() {

                @Override
                public Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh) {
                    return new ArrowOrderByBorder("header.paymentName", "paymentName", stateLocator, dataView, refresh);
                }

                @Override
                public Component filter() {
                    return new TextField<String>("paymentName");
                }

                @Override
                public Component field(final Item<RequestFileGroup> item) {
                    return new Label("paymentName", "") {

                        @Override
                        protected void onBeforeRender() {
                            final RequestFile paymentFile = item.getModelObject().getPaymentFile();
                            final Long paymentFileId = paymentFile.getId();
                            if (paymentFileId != null) {
                                this.replaceWith(new BookmarkablePageLinkPanel<RequestFile>("paymentName",
                                        paymentFile.getName(),
                                        ScrollListBehavior.SCROLL_PREFIX + String.valueOf(paymentFileId),
                                        PaymentList.class, new PageParameters().set("request_file_id", paymentFileId)));
                            }
                            super.onBeforeRender();
                        }
                    };
                }
            });

            //Файл льгот
            addColumn(new Column() {

                @Override
                public Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh) {
                    return new ArrowOrderByBorder("header.benefitName", "benefitName", stateLocator, dataView, refresh);
                }

                @Override
                public Component filter() {
                    return new TextField<String>("benefitName");
                }

                @Override
                public Component field(final Item<RequestFileGroup> item) {
                    return new Label("benefitName", "") {

                        @Override
                        protected void onBeforeRender() {
                            final RequestFile benefitFile = item.getModelObject().getBenefitFile();
                            final Long benefitFileId = benefitFile.getId();
                            if (benefitFileId != null) {
                                this.replaceWith(new BookmarkablePageLinkPanel<RequestFile>("benefitName",
                                        benefitFile.getName(),
                                        ScrollListBehavior.SCROLL_PREFIX + String.valueOf(benefitFileId),
                                        BenefitList.class, new PageParameters().set("request_file_id", benefitFileId)));
                            }
                            super.onBeforeRender();
                        }
                    };
                }
            });
        }

        @Override
        protected String getPreferencePage() {
            return GroupList.class.getName();
        }

        @Override
        protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
            processManagerBean.bindGroup(selectedFileIds, commandParameters);
        }

        @Override
        protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
            processManagerBean.fillGroup(selectedFileIds, commandParameters);
        }

        @Override
        protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
            processManagerBean.saveGroup(selectedFileIds, commandParameters);
        }

        @Override
        protected void load(long userOrganizationId, long osznId, DateParameter dateParameter) {
            processManagerBean.loadGroup(userOrganizationId, osznId,
                    dateParameter.getMonthFrom(), dateParameter.getMonthTo(), dateParameter.getYear());
        }

        @Override
        protected MonthParameterViewMode getLoadMonthParameterViewMode() {
            return MonthParameterViewMode.RANGE;
        }

        @Override
        protected Long getCount(RequestFileGroupFilter filter) {
            return requestFileGroupBean.getRequestFileGroupsCount(filter);
        }

        @Override
        protected List<RequestFileGroup> getObjects(RequestFileGroupFilter filter) {
            return requestFileGroupBean.getRequestFileGroups(filter);
        }

        @Override
        protected Date getLoaded(RequestFileGroup object) {
            return object.getLoaded();
        }

        @Override
        protected long getOsznId(RequestFileGroup object) {
            return object.getOrganizationId();
        }

        @Override
        protected long getUserOrganizationId(RequestFileGroup object) {
            return object.getUserOrganizationId();
        }

        @Override
        protected int getMonth(RequestFileGroup object) {
            return object.getMonth();
        }

        @Override
        protected int getYear(RequestFileGroup object) {
            return object.getYear();
        }

        @Override
        protected int getLoadedRecordCount(RequestFileGroup object) {
            return object.getLoadedRecordCount();
        }

        @Override
        protected int getBindedRecordCount(RequestFileGroup object) {
            return object.getBindedRecordCount();
        }

        @Override
        protected int getFilledRecordCount(RequestFileGroup object) {
            return object.getFilledRecordCount();
        }

        @Override
        protected RequestFileGroup getById(long id) {
            return requestFileGroupBean.getRequestFileGroup(id);
        }

        @Override
        protected void delete(RequestFileGroup object) {
            requestFileGroupBean.delete(object);
        }

        @Override
        protected RequestFile getRequestFile(RequestFileGroup object) {
            return object.getPaymentFile();
        }

        @Override
        protected void logSuccessfulDeletion(RequestFileGroup group) {
            logger().info("Request file group (ID : {}, full name: '{}') has been deleted.", group.getId(), group.getFullName());
            logBean.info(Module.NAME, GroupList.class, RequestFileGroup.class, null, group.getId(),
                    Log.EVENT.REMOVE, group.getLogChangeList(), "Файлы удалены успешно. Имя объекта: {0}",
                    group.getLogObjectName());
        }

        @Override
        protected void logFailDeletion(RequestFileGroup group, Exception e) {
            logger().error("Cannot delete request file group (ID : " + group.getId() + ", full name: '" + group.getFullName() + "').", e);
            logBean.error(Module.NAME, GroupList.class, RequestFileGroup.class, null, group.getId(),
                    Log.EVENT.REMOVE, group.getLogChangeList(), "Ошибка удаления. Имя объекта: {0}",
                    group.getLogObjectName());
        }

        private Logger logger() {
            return LoggerFactory.getLogger(getWebPage().getClass());
        }

        @Override
        protected void showMessages(AjaxRequestTarget target) {
            for (RequestFile rf : processManagerBean.getLinkError(LOAD_GROUP, true)) {
                error(getStringFormat("request_file.link_error", rf.getFullName()));
            }
        }

        @Override
        protected Long[] getOsznOrganizationTypes() {
            return new Long[]{OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE};
        }
    }
    private final GroupListPanel groupListPanel;

    public GroupList(PageParameters params) {
        super(params);

        add(groupListPanel = new GroupListPanel("groupListPanel"));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return groupListPanel.getToolbarButtons(id);
    }
}
