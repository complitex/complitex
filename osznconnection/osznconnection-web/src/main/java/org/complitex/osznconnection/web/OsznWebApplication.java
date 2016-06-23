package org.complitex.osznconnection.web;

import org.apache.wicket.util.time.Duration;
import org.complitex.address.web.sync.AddressSyncPage;
import org.complitex.template.web.ComplitexWebApplication;

/**
 * @author inheaven on 001 01.10.15 17:01
 */
public class OsznWebApplication extends ComplitexWebApplication{
    @Override
    protected void init() {
        super.init();

        getDebugSettings().setAjaxDebugModeEnabled(false);

        mountPage("/address-sync", AddressSyncPage.class);

        getRequestCycleSettings().setTimeout(Duration.minutes(5));
    }
}
