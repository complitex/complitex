package org.complitex.osznconnection.web;

import org.complitex.address.web.sync.AddressSyncPage;
import org.complitex.template.web.ComplitexWebApplication;

/**
 * @author inheaven on 001 01.10.15 17:01
 */
public class OsznWebApplication extends ComplitexWebApplication{
    @Override
    protected void init() {
        super.init();

        mountPage("/address-sync", AddressSyncPage.class);
    }
}