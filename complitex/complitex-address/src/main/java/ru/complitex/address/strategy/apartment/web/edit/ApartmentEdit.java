package ru.complitex.address.strategy.apartment.web.edit;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.complitex.address.Module;
import ru.complitex.address.strategy.apartment.ApartmentStrategy;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Log;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.service.LogBean;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.web.component.DomainObjectComponentUtil;
import ru.complitex.common.web.component.DomainObjectInputPanel;
import ru.complitex.common.web.component.RangeNumbersPanel;
import ru.complitex.common.web.component.RangeNumbersPanel.NumbersList;
import ru.complitex.common.web.component.css.CssAttributeBehavior;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.component.toolbar.search.CollapsibleInputSearchToolbarButton;
import ru.complitex.template.web.pages.DomainObjectEdit;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public final class ApartmentEdit extends DomainObjectEdit {
    public ApartmentEdit(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(
                ApartmentEdit.class, ApartmentEdit.class.getSimpleName() + ".css")));
    }

    @Override
    protected DomainObjectEditPanel newEditPanel(String id, String entity, String strategy, Long objectId,
            Long parentId, String parentEntity, String backInfoSessionKey) {

        if (objectId == null) { // new object
            return new DomainObjectEditPanel(id, entity, strategy, objectId, parentId, parentEntity, backInfoSessionKey) {

                NumbersList numbersList = new NumbersList();
                RangeNumbersPanel rangeNumbersPanel;
                boolean bulkSaveFlag;

                @Override
                protected DomainObjectInputPanel newInputPanel(String id, DomainObject newObject, String entity,
                        String strategyName, Long parentId, String parentEntity) {
                    return new DomainObjectInputPanel(id, newObject, entity, strategyName, parentId, parentEntity) {

                        @Override
                        protected List<Attribute> getSimpleAttributes(List<Attribute> allAttributes) {
                            return Lists.newArrayList();
                        }

                        @Override
                        protected void addComplexAttributesPanelBefore(String id) {
                            final IModel<String> labelModel = DomainObjectComponentUtil.labelModel(getStrategy().getEntity().
                                    getAttribute(ApartmentStrategy.NAME).getNames(), getLocale());
                            rangeNumbersPanel = new RangeNumbersPanel(id, labelModel, numbersList);
                            rangeNumbersPanel.add(new CssAttributeBehavior("apartmentRangeNumberPanel"));
                            add(rangeNumbersPanel);
                        }
                    };
                }

                @Override
                protected boolean validate() {
                    if (isNew() && !rangeNumbersPanel.validate()) {
                        return false;
                    }
                    return super.validate();
                }

                private boolean performDefaultValidation(DomainObject object) {
                    return getStrategy().getValidator().validate(object, this);
                }

                @Override
                protected void save(boolean propagate) {
                    final String numbersAsString = numbersList.asString();
                    final List<List<StringValue>> numbers = numbersList.getNumbers();
                    if (numbers.size() > 1) {
                        bulkSaveFlag = true;
                        onInsert();
                        beforeBulkSave(Module.NAME, DomainObjectEditPanel.class, numbersAsString, getLocale());
                        boolean bulkOperationSuccess = true;
                        for (List<StringValue> number : numbers) {
                            final DomainObject currentObject = CloneUtil.cloneObject(getNewObject());
                            currentObject.getAttribute(ApartmentStrategy.NAME).setStringValues(number);
                            if (performDefaultValidation(currentObject)) {
                                try {
                                    getStrategy().insert(currentObject, DateUtil.getCurrentDate());
                                } catch (Exception e) {
                                    bulkOperationSuccess = false;
                                    log().error("", e);
                                    onFailBulkSave(Module.NAME, DomainObjectEditPanel.class, currentObject,
                                            numbersAsString, numbersList.asString(number), getLocale());
                                }
                            } else {
                                onInvalidateBulkSave(Module.NAME, DomainObjectEditPanel.class, currentObject,
                                        numbersAsString, numbersList.asString(number), getLocale());
                            }
                        }
                        afterBulkSave(Module.NAME, DomainObjectEditPanel.class, numbersAsString, bulkOperationSuccess, getLocale());
                        getSession().getFeedbackMessages().clear();
                    } else {
                        onInsert();
                        final DomainObject object = getNewObject();
                        object.getAttribute(ApartmentStrategy.NAME).setStringValues(numbers.get(0));
                        getStrategy().insert(getNewObject(), DateUtil.getCurrentDate());
                    }

                    if (!bulkSaveFlag) {
                        getLogBean().log(Log.STATUS.OK, Module.NAME, DomainObjectEditPanel.class,
                                isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT, getStrategy(),
                                getOldObject(), getNewObject(), null);
                    }
                    back();
                }

                @Override
                protected void back() {
                    if (bulkSaveFlag) {
                        //return to list page for current entity.
                        PageParameters listPageParams = getStrategy().getListPageParams();
                        setResponsePage(getStrategy().getListPage(), listPageParams);
                    } else {
                        super.back();
                    }
                }
            };
        } else {
            return super.newEditPanel(id, entity, strategy, objectId, parentId, parentEntity, backInfoSessionKey);
        }
    }
    private static final String RESOURCE_BUNDLE = ApartmentEdit.class.getName();

    private static LogBean getLogBean() {
        return EjbBeanLocator.getBean(LogBean.class);
    }

    public static void beforeBulkSave(String moduleName, Class<?> controllerClass, String numbers,
            Locale locale) {
        getLogBean().info(moduleName, controllerClass, DomainObject.class, "apartment", null, Log.EVENT.BULK_SAVE,
                null, ResourceUtil.getString(RESOURCE_BUNDLE, "apartment_bulk_save_start", locale), numbers);
    }

    public static void afterBulkSave(String moduleName, Class<?> controllerClass, String numbers,
            boolean operationSuccessed, Locale locale) {
        LogBean logBean = getLogBean();
        if (operationSuccessed) {
            logBean.info(moduleName, controllerClass, DomainObject.class, "apartment", null, Log.EVENT.BULK_SAVE,
                    null, ResourceUtil.getString(RESOURCE_BUNDLE, "apartment_bulk_save_success_finish", locale), numbers);
        } else {
            logBean.error(moduleName, controllerClass, DomainObject.class, "apartment", null, Log.EVENT.BULK_SAVE,
                    null, ResourceUtil.getString(RESOURCE_BUNDLE, "apartment_bulk_save_fail_finish", locale), numbers);
        }
    }

    public static void onFailBulkSave(String moduleName, Class<?> controllerClass, DomainObject failObject,
            String numbers, String failNumber, Locale locale) {
        getLogBean().log(Log.STATUS.ERROR, moduleName, controllerClass, Log.EVENT.CREATE,
                EjbBeanLocator.getBean(ApartmentStrategy.class), null, failObject,
                ResourceUtil.getString(RESOURCE_BUNDLE, "apartment_bulk_save_fail", locale), numbers, failNumber);
    }

    public static void onInvalidateBulkSave(String moduleName, Class<?> controllerClass,
            DomainObject invalidObject, String numbers, String invalidNumber, Locale locale) {
        getLogBean().log(Log.STATUS.WARN, moduleName, controllerClass, Log.EVENT.CREATE,
                EjbBeanLocator.getBean(ApartmentStrategy.class), null, invalidObject,
                ResourceUtil.getString(RESOURCE_BUNDLE, "apartment_bulk_save_invalid", locale), numbers, invalidNumber);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = Lists.newArrayList();
        toolbarButtons.addAll(super.getToolbarButtons(id));
        toolbarButtons.add(new CollapsibleInputSearchToolbarButton(id));
        return toolbarButtons;

    }
}
