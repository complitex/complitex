package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.service.IProcessListener;
import ru.complitex.keconnection.heatmeter.entity.Heatmeter;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPayload;
import ru.complitex.keconnection.heatmeter.entity.Tablegram;
import ru.complitex.keconnection.heatmeter.entity.TablegramRecord;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static ru.complitex.common.util.DateUtil.getFirstDayOfMonth;
import static ru.complitex.keconnection.heatmeter.entity.HeatmeterType.HEATING;
import static ru.complitex.keconnection.heatmeter.entity.TablegramRecordStatus.*;
import static ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.10.12 12:42
 */
@Stateless
public class TablegramService {
    @EJB
    private TablegramBean tablegramBean;

    @EJB
    private TablegramRecordBean tablegramRecordBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private HeatmeterPayloadBean heatmeterPayloadBean;

    public void process(Tablegram tablegram, IProcessListener<TablegramRecord> listener){
        TablegramRecord current = null;

        try {
            List<TablegramRecord> tablegramRecords = tablegramRecordBean.getTablegramRecords(tablegram.getId());

            for (TablegramRecord tablegramRecord : tablegramRecords){
                current = tablegramRecord;

                process(tablegramRecord, listener, getFirstDayOfMonth(tablegram.getBeginDate()),
                        tablegram.getBeginDate());
            }


        } catch (Exception e) {
            listener.error(current, e);
        }

        listener.done();
    }

    public void process(TablegramRecord tablegramRecord, IProcessListener<TablegramRecord> listener, Date beginOm, Date beginDate){
        if (PROCESSED.equals(tablegramRecord.getStatus()) || ERROR_PAYLOAD_SUM.equals(tablegramRecord.getStatus())){
            if (listener != null) {
                listener.skip(tablegramRecord);
            }

            return;
        }

        Heatmeter heatmeter = heatmeterBean.getHeatmeterByLs(tablegramRecord.getLs(), KE_ORGANIZATION_OBJECT_ID);

        if (heatmeter == null){
            tablegramRecord.setStatus(HEATMETER_NOT_FOUND);

            if (listener != null) {
                listener.skip(tablegramRecord);
            }
        }else {
            tablegramRecord.setHeatmeterId(heatmeter.getId());

            if (heatmeterPayloadBean.isExist(heatmeter.getId())){
                tablegramRecord.setStatus(ALREADY_HAS_PAYLOAD);

                if (listener != null) {
                    listener.skip(tablegramRecord);
                }
            }else {
                //create heatmeterPayload
                HeatmeterPayload heatmeterPayload = new HeatmeterPayload(heatmeter.getId(), beginOm);

                heatmeterPayload.setBeginDate(beginDate);
                heatmeterPayload.setTablegramRecordId(tablegramRecord.getId());
                heatmeterPayload.setPayload1(tablegramRecord.getPayload1());
                heatmeterPayload.setPayload2(tablegramRecord.getPayload2());
                heatmeterPayload.setPayload3(tablegramRecord.getPayload3());

                heatmeterPayloadBean.save(heatmeterPayload, beginOm);

                //update table record
                tablegramRecord.setStatus(PROCESSED);

                //update heatmeter type
                heatmeterBean.updateHeatmeterType(heatmeter.getId(), HEATING);

                //processed
                if (listener != null) {
                    listener.processed(tablegramRecord);
                }
            }
        }

        tablegramRecordBean.save(tablegramRecord);
    }

    public void rollback(Tablegram tablegram, IProcessListener<Tablegram> listener){
        try {
            heatmeterPayloadBean.deleteByTablegramId(tablegram.getId());
            tablegramRecordBean.rollback(tablegram.getId());

            listener.processed(tablegram);
        } catch (Exception e) {
            listener.error(tablegram, e);
        }

        listener.done();
    }
}
