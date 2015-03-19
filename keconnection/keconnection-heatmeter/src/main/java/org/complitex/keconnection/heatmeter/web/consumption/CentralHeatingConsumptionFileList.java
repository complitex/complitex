package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.commons.codec.binary.Hex;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.crypt.Base64;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.service.consumption.ConsumptionFileBean;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionService;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author inheaven on 016 16.03.15 18:46
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class CentralHeatingConsumptionFileList extends AbstractConsumptionFileList{
    private Logger log = LoggerFactory.getLogger(CentralHeatingConsumptionFileList.class);

    @EJB
    private ConsumptionFileBean consumptionFileBean;

    @EJB
    private CentralHeatingConsumptionService centralHeatingConsumptionService;

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
    protected void onUpload(AjaxRequestTarget target, Date om, Long serviceProviderId, Long serviceId, FileUploadField fileUploadField) {
        fileUploadField.getFileUploads().forEach(f ->{
            try {
                centralHeatingConsumptionService.load(om, serviceProviderId, serviceId, f.getClientFileName(),
                        Hex.encodeHexString(f.getMD5()), f.getInputStream());

                info(getStringFormat("info_loaded", f.getClientFileName()));

                target.add(getFilteredDataTable());
                target.add(getMessages());
            } catch (IOException e) {
                log.error("file {} upload error", f.getClientFileName(), e);
            }
        });
    }
}
