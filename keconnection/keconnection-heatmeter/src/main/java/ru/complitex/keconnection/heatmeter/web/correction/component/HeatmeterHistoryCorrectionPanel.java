package ru.complitex.keconnection.heatmeter.web.correction.component;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;

import java.util.List;

/**
 *
 * @author Artem
 */
public final class HeatmeterHistoryCorrectionPanel extends Panel {

    public HeatmeterHistoryCorrectionPanel(String id, List<HeatmeterCorrection> historyCorrections) {
        super(id);

        add(new ListView<HeatmeterCorrection>("corrections", historyCorrections) {

            @Override
            protected void populateItem(ListItem<HeatmeterCorrection> item) {
                final HeatmeterCorrection correction = item.getModelObject();
                item.add(new HeatmeterItemCorrectionPanel("heatmeterItemCorrectionPanel", correction));
            }
        });
    }
}
