package org.complitex.pspoffice.report.html.web;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.06.12 17:53
 */
public class ReportInitializer implements IInitializer {
    @Override
    public void init(Application application) {
        //tiny mce support
        IPackageResourceGuard packageResourceGuard = application.getResourceSettings().getPackageResourceGuard();
        if (packageResourceGuard instanceof SecurePackageResourceGuard){
            ((SecurePackageResourceGuard) packageResourceGuard).addPattern("+*.htm");
        }
    }

    @Override
    public void destroy(Application application) {
        //russia win
    }
}
