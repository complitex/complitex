package org.complitex.osznconnection.file.web;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.Log;
import org.complitex.common.service.LogBean;
import org.complitex.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.scroll.ScrollListBehavior;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel.MonthParameterViewMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;

public abstract class AbstractFileListPanel extends AbstractProcessableListPanel<RequestFile, RequestFileFilter> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LogBean logBean;

    public AbstractFileListPanel(String id, ProcessType loadProcessType, ProcessType bindProcessType,
                                 ProcessType fillProcessType, ProcessType saveProcessType) {
        super(id, loadProcessType, bindProcessType, fillProcessType, saveProcessType);

        //Имя
        addColumn(new Column() {

            @Override
            public Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh) {
                return new ArrowOrderByBorder("header.name", "name", stateLocator, dataView, refresh);
            }

            @Override
            public Component filter() {
                return new TextField<String>("name");
            }

            @Override
            public Component field(Item<RequestFile> item) {
                return new BookmarkablePageLinkPanel<RequestFile>("name", item.getModelObject().getFullName(),
                        ScrollListBehavior.SCROLL_PREFIX + String.valueOf(item.getModelObject().getId()), getItemListPageClass(),
                        new PageParameters().set("request_file_id", item.getModelObject().getId()));
            }
        });
    }

    @Override
    protected void logSuccessfulDeletion(RequestFile requestFile) {
        final long requestFileId = requestFile.getId();
        logger().info("Request file (ID : {}, full name: '{}') has been deleted.", requestFileId, requestFile.getFullName());
        logBean.info(Module.NAME, getWebPage().getClass(), RequestFile.class, null, requestFileId,
                Log.EVENT.REMOVE, requestFile.getLogChangeList(), "Файл удален успешно. Имя объекта: {0}",
                requestFile.getLogObjectName());
    }

    @Override
    protected void logFailDeletion(RequestFile requestFile, Exception e) {
        final long requestFileId = requestFile.getId();
        logger().error("Cannot delete request file (ID : " + requestFileId + ", full name: '" + requestFile.getFullName() + "').", e);
        logBean.error(Module.NAME, getWebPage().getClass(), RequestFile.class, null, requestFileId,
                Log.EVENT.REMOVE, requestFile.getLogChangeList(), "Ошибка удаления. Имя объекта: {0}",
                requestFile.getLogObjectName());
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getWebPage().getClass());
    }

    @Override
    protected Long getCount(RequestFileFilter filter) {
        return requestFileBean.getCount(filter);
    }

    @Override
    protected List<RequestFile> getObjects(RequestFileFilter filter) {
        return requestFileBean.getRequestFiles(filter);
    }

    @Override
    protected RequestFile getObject(long id) {
        return requestFileBean.getRequestFile(id);
    }

    @Override
    protected void delete(RequestFile requestFile) {
        requestFileBean.delete(requestFile);
    }

    @Override
    protected RequestFile getRequestFile(RequestFile object) {
        return object;
    }

    @Override
    protected MonthParameterViewMode getLoadMonthParameterViewMode() {
        return MonthParameterViewMode.RANGE;
    }

    protected abstract Class<? extends WebPage> getItemListPageClass();
}
