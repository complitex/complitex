package ru.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.web.component.LabelTextField;
import ru.complitex.common.web.component.wiquery.ExtendedDialog;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.RequestFileType;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.example.SubsidyExample;
import ru.complitex.osznconnection.file.entity.subsidy.Subsidy;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidySum;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescription;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;
import ru.complitex.osznconnection.file.service.file_description.RequestFileFieldDescription;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyService;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.complitex.osznconnection.file.entity.RequestStatus.SUBSIDY_NM_PAY_ERROR;
import static ru.complitex.osznconnection.file.entity.subsidy.SubsidyDBF.*;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.12.13 18:06
 */
public class SubsidyEditDialog extends Panel {
    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidyService subsidyService;

    @EJB
    private RequestFileBean requestFileBean;

    private ExtendedDialog dialog;
    private IModel<Subsidy> subsidyModel = Model.of(new Subsidy());
    private Form form;

    private Map<String, LabelTextField> textFieldMap = new HashMap<>();

    private Label nmPayDiff, summaDiff, subsDiff;

    private AjaxSubmitLink link;

    public SubsidyEditDialog(String id, final Component toUpdate) {
        super(id);

        dialog = new ExtendedDialog("dialog"){
            @Override
            protected void onClose(AjaxRequestTarget target) {
                target.add(toUpdate);
            }
        };

        dialog.setModal(true);
        dialog.setWidth(750);
        add(dialog);

        form = new Form("form"){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();

                if (subsidyModel.getObject().getUserOrganizationId() != null) {
                    SubsidyEditDialog.this.validate(null);
                }
            }
        };
        form.setOutputMarkupId(true);
        dialog.add(form);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        form.add(messages);

        final RequestFileDescription description = requestFileDescriptionBean.getFileDescription(RequestFileType.SUBSIDY);

        int index = -1;
        for (RequestFileFieldDescription d : description.getFields()){
            index++;

            String name = d.getName();

            //CYR
            if (Subsidy.CYR.contains(name)){
                name += "_CYR";
            }

            LabelTextField textField = new LabelTextField<Object>(name, d.getLength(),
                    new PropertyModel<>(subsidyModel, "dbfFields[" + name + "]"), index){
                @Override
                public boolean isEnabled() {
                    return getIndex() > 11 && SUBSIDY_NM_PAY_ERROR.equals(subsidyModel.getObject().getStatus());
                }
            };
            textField.setType(d.getFieldType());
            textField.setOutputMarkupId(true);

            if (index > 11){
                textField.add(new AjaxFormComponentUpdatingBehavior("change") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        validate(target);
                    }
                });
            }

            textFieldMap.put(d.getName(), textField);

            form.add(textField);
        }

        //Diffs
        form.add(nmPayDiff = new Label("nmPayDiff", Model.of("")));
        nmPayDiff.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        form.add(summaDiff = new Label("summaDiff", Model.of("")));
        summaDiff.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        form.add(subsDiff = new Label("subsDiff", Model.of("")));
        subsDiff.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);

        form.add(link = new AjaxSubmitLink("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Subsidy subsidy = subsidyModel.getObject();

                RequestFile requestFile = requestFileBean.getRequestFile(subsidy.getRequestFileId());

                if (SUBSIDY_NM_PAY_ERROR.equals(subsidy.getStatus())) {
                    subsidy.setStatus(RequestStatus.LOADED);

                    //save
                    Map<String, Object> updateFields = new HashMap<>();
                    int index = 0;
                    for (RequestFileFieldDescription d : description.getFields()){
                        if (++index > 11){
                            updateFields.put(d.getName(), subsidy.getDbfFields().get(d.getName()));
                        }
                    }
                    subsidy.setUpdateFieldMap(updateFields);

                    subsidyBean.update(subsidy);

                    //update request file status
                    if (RequestFileStatus.LOAD_ERROR.equals(requestFile.getStatus())) {
                        SubsidyExample loaded = new SubsidyExample();
                        loaded.setRequestFileId(subsidy.getRequestFileId());
                        loaded.setStatus(RequestStatus.LOADED);

                        SubsidyExample all = new SubsidyExample();
                        all.setRequestFileId(subsidy.getRequestFileId());

                        if (Objects.equals(subsidyBean.getCount(loaded), subsidyBean.getCount(all))) {
                            requestFile.setStatus(RequestFileStatus.LOADED);

                            requestFileBean.save(requestFile);
                        }
                    }
                }

                dialog.close(target);

                getSession().info(subsidy.getField(SubsidyDBF.RASH) + " " + subsidy.getField(SubsidyDBF.FIO) + ": "
                        + getString("info_updated"));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }

            @Override
            public boolean isVisible() {
                return SUBSIDY_NM_PAY_ERROR.equals(subsidyModel.getObject().getStatus());
            }
        });
        link.setOutputMarkupId(true);

        form.add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
                target.add(toUpdate);
            }
        });

        form.add(new AjaxLink("proportionally") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Subsidy subsidy = subsidyModel.getObject();

                BigDecimal summa = (BigDecimal)subsidy.getField(SubsidyDBF.SUMMA);

                BigDecimal sumSBn = new BigDecimal(0);
                for (int i = 1; i <= 8; ++i){
                    sumSBn = sumSBn.add((BigDecimal)subsidy.getField("SB" + i));
                }

                for (int i = 1; i <= 8; ++i){
                    BigDecimal sm = summa.multiply((BigDecimal) subsidy.getField("SB" + i))
                            .divide(sumSBn, 2, RoundingMode.HALF_UP);
                    subsidy.putField("SM" + i, sm);

                    target.add(textFieldMap.get("SM" + i));
                }

                //check sum
                BigDecimal sumSMn = new BigDecimal(0);
                for (int i = 1; i <= 8; ++i){
                    sumSMn = sumSMn.add((BigDecimal)subsidy.getField("SM" + i));
                }

                int diff = summa.subtract(sumSMn).multiply(new BigDecimal(100)).intValue();

                if (diff != 0){
                    BigDecimal one = new BigDecimal(diff < 0 ? "-0.01" : "0.01");

                    for (int i = 0; i < Math.abs(diff); ++i){
                        int index = (i % 8) + 1;

                        subsidy.putField("SM" + index, ((BigDecimal)subsidy.getField("SM" + index)).add(one));
                    }
                }
            }

            @Override
            public boolean isVisible() {
                return SUBSIDY_NM_PAY_ERROR.equals(subsidyModel.getObject().getStatus());
            }
        });

        form.add(new AjaxLink("recalculate") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Subsidy subsidy = subsidyModel.getObject();

                SubsidySum subsidySum = subsidyService.getSubsidySum(subsidy);

                subsidy.putField(NM_PAY, subsidySum.getNSum());
                subsidy.putField(SUMMA, subsidySum.getSmSum());
                subsidy.putField(SUBS, subsidySum.getSbSum());

                validate(target);
            }

            @Override
            public boolean isVisible() {
                return SUBSIDY_NM_PAY_ERROR.equals(subsidyModel.getObject().getStatus());
            }
        });
    }

    public void open(AjaxRequestTarget target, Long subsidyId){
        subsidyModel.setObject(subsidyBean.getSubsidy(subsidyId));

        target.add(form);
        dialog.open(target);
    }

    private void validate(final AjaxRequestTarget target){
        Subsidy subsidy = subsidyModel.getObject();

        SubsidySum subsidySum = subsidyService.getSubsidySum(subsidy);

        int numm = subsidy.getField("NUMM") != null ? ((Number)subsidy.getField("NUMM")).intValue() : 0;

        BigDecimal summa = (BigDecimal) subsidy.getField("SUMMA");
        BigDecimal subs = (BigDecimal) subsidy.getField("SUBS");
        BigDecimal nmPay = (BigDecimal) subsidy.getField("NM_PAY");

        LabelTextField nmPayTextField = textFieldMap.get("NM_PAY");
        LabelTextField summaTextField = textFieldMap.get("SUMMA");
        LabelTextField subsTextField = textFieldMap.get("SUBS");
        LabelTextField nummTextField = textFieldMap.get("NUMM");

        boolean nummCheck =  numm <= 0 || summa.compareTo(subs.multiply(new BigDecimal(numm))) == 0;

        if (nmPay != null && nmPay.compareTo(subsidySum.getNSum()) != 0){
            nmPayTextField.add(AttributeModifier.replace("style", "background-color: lightpink;"));
            nmPayDiff.setDefaultModelObject("(" + nmPay.subtract(subsidySum.getNSum()) + ")");
        }else{
            nmPayTextField.add(AttributeModifier.replace("style", ""));
            nmPayDiff.setDefaultModelObject("");
        }

        if (summa != null && (!nummCheck || summa.compareTo(subsidySum.getSmSum()) != 0)){
            summaTextField.add(AttributeModifier.replace("style", "background-color: lightpink;"));
            summaDiff.setDefaultModelObject("(" + summa.subtract(subsidySum.getSmSum()) + ")");
        }else {
            summaTextField.add(AttributeModifier.replace("style", ""));
            summaDiff.setDefaultModelObject("");
        }

        if (subs != null && (!nummCheck || subs.compareTo(subsidySum.getSbSum()) != 0)){
            subsTextField.add(AttributeModifier.replace("style", "background-color: lightpink;"));
            subsDiff.setDefaultModelObject("(" + subs.subtract(subsidySum.getSbSum()) + ")");
        }else{
            subsTextField.add(AttributeModifier.replace("style", ""));
            subsDiff.setDefaultModelObject("");
        }

        nummTextField.add(AttributeModifier.replace("style", !nummCheck ? "background-color: lightpink;" : ""));

        //save
        boolean enabled = subsidyModel.getObject().getUserOrganizationId() == null
                || subsidyService.validateSum(subsidyModel.getObject());
        link.add(AttributeModifier.replace("style", "opacity:" + (enabled ? "1" : "0.5")));
        link.setEnabled(enabled);


        if (target != null) {
            target.add(nmPayDiff);
            target.add(summaDiff);
            target.add(subsDiff);
            target.add(nmPayTextField);
            target.add(summaTextField);
            target.add(subsTextField);
            target.add(nummTextField);
            target.add(link);
        }
    }
}
