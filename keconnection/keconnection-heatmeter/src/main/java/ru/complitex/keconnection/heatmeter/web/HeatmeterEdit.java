package ru.complitex.keconnection.heatmeter.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.strategy.building.BuildingStrategy;
import ru.complitex.address.strategy.building.entity.BuildingCode;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.exception.AbstractException;
import ru.complitex.common.exception.ConcurrentModificationException;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.EnumDropDownChoice;
import ru.complitex.keconnection.heatmeter.entity.*;
import ru.complitex.keconnection.heatmeter.service.HeatmeterBean;
import ru.complitex.keconnection.heatmeter.service.HeatmeterInputBean;
import ru.complitex.keconnection.heatmeter.service.HeatmeterService;
import ru.complitex.keconnection.heatmeter.web.component.HeatmeterConnectionPanel;
import ru.complitex.keconnection.heatmeter.web.component.HeatmeterInputPanel;
import ru.complitex.keconnection.heatmeter.web.component.HeatmeterOperationPanel;
import ru.complitex.keconnection.heatmeter.web.component.HeatmeterPayloadPanel;
import ru.complitex.keconnection.heatmeter.web.correction.component.HeatmeterCorrectionDialog;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static ru.complitex.common.util.DateUtil.addMonth;
import static ru.complitex.keconnection.heatmeter.entity.HeatmeterValidateStatus.VALID;
import static ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class HeatmeterEdit extends FormTemplatePage {
    private final Logger log = LoggerFactory.getLogger(HeatmeterEdit.class);

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private HeatmeterService heatmeterService;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private HeatmeterInputBean inputBean;

    private IModel<Date> om = new Model<>();
    private Date minOm = null;

    public HeatmeterEdit() {
        init(null);
    }

    public HeatmeterEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }

    private void init(Long id) {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        Heatmeter heatmeter;

        if (id != null) {
            heatmeter = heatmeterBean.getHeatmeter(id);
            om.setObject(heatmeter.getOm());
            minOm = heatmeterBean.getMinOm(heatmeter.getId());

            List<Long> organizationsIds = sessionBean.getUserOrganizationTreeObjectIds();

            if (!sessionBean.isAdmin()) {
                for (HeatmeterConnection connection : heatmeter.getConnections()){
                    if (!organizationsIds.contains(connection.getOrganizationId())){
                        throw new UnauthorizedInstantiationException(HeatmeterEdit.class);
                    }
                }
            }
        } else {
            heatmeter = new Heatmeter();
            heatmeter.setOrganizationId(KE_ORGANIZATION_OBJECT_ID);
        }

        final IModel<Heatmeter> model = new CompoundPropertyModel<>(heatmeter);

        Form form = new Form<>("form", model);
        add(form);

        //Ls
        form.add(new TextField<>("ls"));

        //Type
        form.add(new EnumDropDownChoice<>("type", HeatmeterType.class, false));

        //Calculating
        form.add(new CheckBox("calculating"));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        form.add(container);

        //Operating month
        final WebMarkupContainer omContainer = new WebMarkupContainer("om_container") {

            @Override
            public boolean isVisible() {
                return om.getObject() != null;
            }
        };
        omContainer.setOutputMarkupId(true);
        container.add(omContainer);

        omContainer.add(new Label("current_operation_month", om));

        omContainer.add(new AjaxLink("previous_month") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                om.setObject(addMonth(om.getObject(), -1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return minOm != null && om.getObject().after(minOm);
            }
        });

        omContainer.add(new AjaxLink("next_month") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                om.setObject(addMonth(om.getObject(), 1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return om.getObject().before(model.getObject().getOm());
            }
        });

        //Operation
        container.add(new HeatmeterOperationPanel("operations", model, om));

        //Connection
        container.add(new HeatmeterConnectionPanel("connections", model, om));

        //Payloads
        container.add(new HeatmeterPayloadPanel("payloads", model, om));

        //Input
        container.add(new HeatmeterInputPanel("inputs", model, om));

        //Save
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                try {
                    Heatmeter heatmeter = model.getObject();

                    //set empty payload as zero
                    for (HeatmeterPayload payload : heatmeter.getPayloads()){
                        if (payload.getPayload1() == null){
                            payload.setPayload1(new BigDecimal(0));
                        }
                        if (payload.getPayload2() == null){
                            payload.setPayload2(new BigDecimal(0));
                        }
                        if (payload.getPayload3() == null){
                            payload.setPayload3(new BigDecimal(0));
                        }
                    }

                    //validate
                    HeatmeterValidate validate = heatmeterService.validate(heatmeter);

                    if (!VALID.equals(validate.getStatus())) {
                        error(getStringFormat(validate.getStatus().name().toLowerCase(), validate));

                        return;
                    }

                    //update om for new heatmeter
                    if (heatmeter.getId() == null) {
                        HeatmeterConnection connection = heatmeter.getConnections().get(0);
                        BuildingCode buildingCode = buildingStrategy.getBuildingCodeById(connection.getObjectId());

                        Date om = organizationStrategy.getOperatingMonthDate(buildingCode.getOrganizationId());

                        if (om == null) {
                            error(getStringFormat("error_om_not_found", buildingCode.getBuildingCode()));
                            return;
                        }

                        heatmeter.setOm(om);

                        //update om for periods
                        List<HeatmeterPeriod> periods = heatmeter.getPeriods();
                        for (HeatmeterPeriod p : periods) {
                            p.setBeginOm(om);
                        }
                    }

                    //adjust begin date for previous inputs
                    heatmeterService.adjustInputBeginDate(heatmeter);


                    //recalculate consumptions for inputs
                    heatmeterService.calculateConsumptions(heatmeter);

                    //save
                    heatmeterBean.save(heatmeter);

                    getSession().info(getStringFormat("info_saved", heatmeter.getLs()));
                } catch (ConcurrentModificationException e){
                    log.error("Ошибка сохранения теплосчетчика", e);
                    getSession().error(getString("error_concurrent_modification"));
                } catch (Exception e) {
                    log.error("Ошибка сохранения теплосчетчика", e);
                    getSession().error(new AbstractException(e, "Ошибка сохранения теплосчетчика: {0}", model.getObject().getLs()) {
                    });
                }

                setResponsePage(HeatmeterList.class);
            }
        });

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(HeatmeterList.class);
            }
        });

        final HeatmeterCorrectionDialog heatmeterCorrectionDialog =
                new HeatmeterCorrectionDialog("heatmeterCorrectionDialog", heatmeter);
        add(heatmeterCorrectionDialog);
        AjaxLink<Void> correctionsLink = new AjaxLink<Void>("correctionsLink") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                heatmeterCorrectionDialog.open(target);
            }
        };
        correctionsLink.setVisible(heatmeterCorrectionDialog.isVisible());
        form.add(correctionsLink);
    }
}
