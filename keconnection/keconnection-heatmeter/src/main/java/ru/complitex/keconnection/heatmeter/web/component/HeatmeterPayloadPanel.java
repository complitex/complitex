package ru.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.web.component.LabelDateField;
import ru.complitex.common.web.component.LabelTextField;
import ru.complitex.keconnection.heatmeter.entity.Heatmeter;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPayload;
import ru.complitex.keconnection.heatmeter.service.HeatmeterPayloadBean;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static ru.complitex.common.web.component.LabelTextField.Converter.BIG_DECIMAL_CONVERTER_2;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 16:42
 */
public class HeatmeterPayloadPanel extends AbstractHeatmeterEditPanel {
    @EJB
    private HeatmeterPayloadBean payloadBean;

    public HeatmeterPayloadPanel(String id, final IModel<Heatmeter> model, final IModel<Date> om) {
        super(id, model, om);

        setOutputMarkupId(true);

        final IModel<List<HeatmeterPayload>> payloadsModel = new LoadableDetachableModel<List<HeatmeterPayload>>() {

            @Override
            protected List<HeatmeterPayload> load() {
                Heatmeter heatmeter = model.getObject();

                return isActiveOm()
                        ? heatmeter.getPayloads()
                        : payloadBean.getList(heatmeter.getId(), om.getObject());
            }
        };

        final ListView<HeatmeterPayload> payloads = new ListView<HeatmeterPayload>("list_view", payloadsModel) {

            @Override
            protected void populateItem(ListItem<HeatmeterPayload> item) {
                final HeatmeterPayload payload = item.getModelObject();

                item.add(new LabelDateField("begin_date", new PropertyModel<Date>(payload, "beginDate"), false));
                item.add(new LabelDateField("end_date", new PropertyModel<Date>(payload, "endDate"), false));

                item.add(new LabelTextField<>("payload1", 5, new PropertyModel<BigDecimal>(payload, "payload1"), BIG_DECIMAL_CONVERTER_2));
                item.add(new LabelTextField<>("payload2", 5, new PropertyModel<BigDecimal>(payload, "payload2"), BIG_DECIMAL_CONVERTER_2));
                item.add(new LabelTextField<>("payload3", 5, new PropertyModel<BigDecimal>(payload, "payload3"), BIG_DECIMAL_CONVERTER_2));

                item.add(new AjaxSubmitLink("remove") {

                    @Override
                    public boolean isVisible() {
                        return isActiveOm() && !payloadsModel.getObject().isEmpty();
                    }

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        model.getObject().getPayloads().remove(payload);
                        target.add(HeatmeterPayloadPanel.this);
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                    }
                });

                item.visitChildren(new IVisitor<Component, Object>() {

                    @Override
                    public void component(Component object, IVisit<Object> visit) {
                        object.setEnabled(isActiveOm());
                        visit.dontGoDeeper();
                    }
                });
            }
        };
        add(payloads);

        add(new WebMarkupContainer("header") {

            @Override
            public boolean isVisible() {
                return !payloadsModel.getObject().isEmpty();
            }
        });

        add(new AjaxSubmitLink("add") {

            @Override
            public boolean isVisible() {
                return isActiveOm();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                HeatmeterPayload payload = new HeatmeterPayload();
                payload.setHeatmeterId(model.getObject().getId());
                payload.setBeginOm(om.getObject());
                payload.setBeginDate(DateUtil.getCurrentDate());
                model.getObject().getPayloads().add(payload);

                target.add(HeatmeterPayloadPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
