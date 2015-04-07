package org.complitex.keconnection.web;

import org.complitex.keconnection.heatmeter.web.consumption.CentralHeatingConsumptionFileList;
import org.complitex.template.web.ComplitexWebApplication;

/**
 * @author inheaven on 007 07.04.15 19:08
 */
public class KeWebApplication extends ComplitexWebApplication{
    @Override
    protected void init() {
        super.init();

        mountPage("/central-heating-consumptions", CentralHeatingConsumptionFileList.class);
    }
}
