package org.complitex.pspoffice.importing;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "PspImportModule")
@Startup
public class Module {
    public static final String NAME = "org.complitex.pspoffice.importing";
}
