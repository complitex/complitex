package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.service.consumption.ConsumptionFileBean;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.UploadButton;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

/**
 * @author inheaven on 016 16.03.15 18:46
 */
public class CentralHeatingConsumptionFileList extends AbstractConsumptionFileList{
    @EJB
    private ConsumptionFileBean consumptionFileBean;

    @Override
    protected void onView(AjaxRequestTarget target, IModel<ConsumptionFile> model) {
        setResponsePage(CentralHeatingConsumptionList.class, new PageParameters().add("id", model.getObject().getId()));
    }

    @Override
    protected List<ConsumptionFile> getList(FilterWrapper<ConsumptionFile> filterWrapper) {
        return consumptionFileBean.getConsumptionFiles(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<ConsumptionFile> filterWrapper) {
        return consumptionFileBean.getConsumptionFilesCount(filterWrapper);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(
                new UploadButton(id) {

                    @Override
                    protected void onClick() {
                        //todo open upload dialog
                    }
                });
    }
}
