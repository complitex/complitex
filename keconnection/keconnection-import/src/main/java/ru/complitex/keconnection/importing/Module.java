package ru.complitex.keconnection.importing;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "KeConnectionImportModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.keconnection.importing";
}
