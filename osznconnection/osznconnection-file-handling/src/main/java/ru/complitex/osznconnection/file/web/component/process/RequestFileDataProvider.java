package ru.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.model.IModel;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileFilter;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.template.web.template.TemplatePage;

import java.util.List;

public class RequestFileDataProvider extends DataProvider<RequestFile> {

    private final TemplatePage page;
    private final IModel<RequestFileFilter> model;
    private final SelectManager selectManager;

    public RequestFileDataProvider(TemplatePage page,
            IModel<RequestFileFilter> model, SelectManager selectManager) {
        this.page = page;
        this.model = model;
        this.selectManager = selectManager;
    }

    private RequestFileBean requestFileBean() {
        return EjbBeanLocator.getBean(RequestFileBean.class);
    }

    @Override
    protected Iterable<? extends RequestFile> getData(long first, long count) {
        final RequestFileFilter filter = model.getObject();

        //store preference, but before clear data order related properties.
        {
            filter.setAscending(false);
            filter.setSortProperty(null);
            page.setFilterObject(filter);
        }

        //prepare filter object
        filter.setFirst(first);
        filter.setCount(count);
        filter.setSortProperty(getSort().getProperty());
        filter.setAscending(getSort().isAscending());

        List<RequestFile> requestFiles = requestFileBean().getRequestFiles(filter);

        selectManager.initializeSelectModels(requestFiles);

        return requestFiles;
    }

    @Override
    protected Long getSize() {
        return requestFileBean().getCount(model.getObject());
    }
}
