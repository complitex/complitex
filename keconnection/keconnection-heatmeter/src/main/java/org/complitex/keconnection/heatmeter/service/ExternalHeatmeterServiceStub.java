package org.complitex.keconnection.heatmeter.service;

import org.complitex.keconnection.heatmeter.entity.cursor.ComMeter;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeterCursor;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.stream.IntStream;

/**
 * @author inheaven on 08.06.2015 21:56.
 */
@Stateless
public class ExternalHeatmeterServiceStub {
    public void callComMeterCursor(ComMeterCursor comMeterCursor){
        comMeterCursor.setResultCode(1);

        IntStream.range(1, 10).forEach(i -> {
            ComMeter comMeter = new ComMeter();
            comMeter.setMId(i);
            comMeter.setMNum(i);
            comMeter.setMType(i);
            comMeter.setMDate(new Date());

            comMeterCursor.getData().add(comMeter);
        });
    }
}
