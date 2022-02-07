package ru.complitex.keconnection.heatmeter;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "HeatmeterModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.keconnection.heatmeter";
}
