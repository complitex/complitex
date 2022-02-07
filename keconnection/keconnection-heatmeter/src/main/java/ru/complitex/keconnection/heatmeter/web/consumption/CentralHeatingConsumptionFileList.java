package ru.complitex.keconnection.heatmeter.web.consumption;

import org.apache.commons.codec.binary.Hex;
import org.apache.poi.util.IOUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import ru.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionService;
import ru.complitex.keconnection.heatmeter.service.consumption.ConsumptionFileBean;
import ru.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.ByteArrayInputStream;
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
    protected Class<? extends Page> getViewPage() {
        return CentralHeatingConsumptionList.class;
    }

    @Override
    protected PageParameters getViewPageParameters(IModel<ConsumptionFile> model) {
        return new PageParameters().add("id", model.getObject().getId());
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
    protected void onUpload(AjaxRequestTarget target, Date om, Long serviceProviderId, Long serviceId,
                            Long userOrganizationId, FileUploadField fileUploadField) {
        fileUploadField.getFileUploads().forEach(f ->{
            try {
                centralHeatingConsumptionService.load(om, serviceProviderId, serviceId, userOrganizationId,
                        f.getClientFileName(), Hex.encodeHexString(f.getMD5()),
                        new ByteArrayInputStream(IOUtils.toByteArray(f.getInputStream())));
            } catch (IOException e) {
                log.error("file {} upload error", f.getClientFileName(), e);
            }
        });
    }

    @Override
    protected void onBind(AjaxRequestTarget target, List<ConsumptionFile> consumptionFiles) {
        consumptionFiles.forEach(centralHeatingConsumptionService::bind);
    }
}
