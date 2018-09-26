package org.complitex.osznconnection.file.web.component.account;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.complitex.common.entity.Cursor;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.osznconnection.file.entity.AccountDetail;
import org.complitex.osznconnection.file.entity.Lodger;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.apache.wicket.model.Model.of;

/**
 * Панель для показа возможных вариантов выбора л/c по детальной информации,
 * когда больше одного человека в ЦН, имеющие разные номера л/c, привязаны к одному адресу.
 * @author Artem
 */
public class AccountNumberPickerPanel extends Panel {
    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    private static class TextFieldUpdating extends OnChangeAjaxBehavior{
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            //update model
        }
    }


    private IModel<List<AccountDetail>> accountDetailsModel;
    private IModel<AccountDetail> accountDetailModel;

    private IModel<Date> dateModel;
    private IModel<Long> userOrganizationId;

    public AccountNumberPickerPanel(String id, IModel<List<AccountDetail>> accountDetailsModel,
            IModel<AccountDetail> accountDetailModel, IModel<Date> dateModel, IModel<Long> userOrganizationId) {
        super(id);
        this.accountDetailsModel = accountDetailsModel;
        this.accountDetailModel = accountDetailModel;
        this.dateModel = dateModel;
        this.userOrganizationId = userOrganizationId;

        setOutputMarkupId(true);

        init();
    }

    private void init() {
        RadioGroup<AccountDetail> radioGroup = new RadioGroup<AccountDetail>("radioGroup", accountDetailModel){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();

                List<AccountDetail> list = accountDetailsModel.getObject();

                if (list != null && list.size() == 1){
                    getModel().setObject(list.get(0));
                }
            }
        };
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        add(radioGroup);

        IModel<AccountDetail> filterModel = Model.of(new AccountDetail());

        add(new TextField<>("accCodeFilter", new PropertyModel<>(filterModel, "accCode")).add(new TextFieldUpdating()));
        add(new TextField<>("zheuFilter", new PropertyModel<>(filterModel, "zheu")).add(new TextFieldUpdating()));
        add(new TextField<>("zheuCodeFilter", new PropertyModel<>(filterModel, "zheuCode")).add(new TextFieldUpdating()));
        add(new TextField<>("ownerFioFilter", new PropertyModel<>(filterModel, "ownerFio")).add(new TextFieldUpdating()));
        add(new TextField<>("addressFilter", new PropertyModel<>(filterModel, "address")).add(new TextFieldUpdating()));
        add(new TextField<>("ownerInnFilter", new PropertyModel<>(filterModel, "ownerINN")).add(new TextFieldUpdating()));
        add(new TextField<>("ercCodeFilter", new PropertyModel<>(filterModel, "ercCode")).add(new TextFieldUpdating()));

        add(new AjaxLink("find") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(AccountNumberPickerPanel.this);
            }
        });

        add(new AjaxLink("filter_reset") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                filterModel.setObject(new AccountDetail());

                target.add(AccountNumberPickerPanel.this);
            }
        });

        DataProvider<AccountDetail> dataProvider = new DataProvider<AccountDetail>() {
            private List<AccountDetail> getList(){
                Iterable<? extends AccountDetail> it = Iterables.filter(accountDetailsModel.getObject(),
                        new Predicate<AccountDetail>() {
                            private boolean apply(String f, String input){
                                return f == null || (input != null && input.contains(f));
                            }

                            @Override
                            public boolean apply(AccountDetail input) {
                                AccountDetail f = filterModel.getObject();

                                return apply(f.getAccCode(), input.getAccCode())
                                        && apply(f.getAccCode(), input.getAccCode())
                                        && apply(f.getZheu(), input.getZheu())
                                        && apply(f.getZheuCode(), input.getZheuCode())
                                        && apply(f.getOwnerFio(), input.getOwnerFio())
                                        && apply(f.getAddress(), input.displayAddress(getLocale()))
                                        && apply(f.getOwnerINN(), input.getOwnerINN())
                                        && apply(f.getErcCode(), input.getErcCode());
                            }
                        });

                return Lists.newArrayList(it);
            }

            @Override
            protected Iterable<? extends AccountDetail> getData(long first, long count) {
                List<AccountDetail> list = getList().subList((int)first, (int)(first + count));

                Collections.sort(list, new Comparator<AccountDetail>() {
                    @Override
                    public int compare(AccountDetail o1, AccountDetail o2) {
                        if (getSort() != null) {
                            boolean asc = getSort().isAscending();

                            switch (getSort().getProperty()){
                                case "accCode": return StringUtil.compare(o1.getAccCode(), o2.getAccCode(), asc);
                                case "zheu": return StringUtil.compare(o1.getZheu(), o2.getZheu(), asc);
                                case "zheuCode": return StringUtil.compare(o1.getZheuCode(), o2.getZheuCode(), asc);
                                case "ownerFio": return StringUtil.compare(o1.getOwnerFio(), o2.getOwnerFio(), asc);
                                case "address": return StringUtil.compare(o1.displayAddress(getLocale()), o2.displayAddress(getLocale()), asc);
                                case "ownerInn": return StringUtil.compare(o1.getOwnerINN(), o2.getOwnerINN(), asc);
                                case "ercCode": return StringUtil.compare(o1.getErcCode(), o2.getErcCode(), asc);
                            }
                        }

                        return 0;
                    }
                });

                return list;
            }

            @Override
            protected Long getSize() {
                return (long) getList().size();
            }
        };

        DataView<AccountDetail> accountDetails = new DataView<AccountDetail>("accountDetails", dataProvider) {

            @Override
            protected void populateItem(Item<AccountDetail> item) {
                AccountDetail detail = item.getModelObject();

                item.add(new Radio<>("radio", item.getModel(), radioGroup));
                item.add(new Label("accCode", of(detail.getAccCode())));
                item.add(new Label("zheu", of(detail.getZheu())));
                item.add(new Label("zheuCode", of(detail.getZheuCode())));
                item.add(new Label("name", of(detail.getOwnerFio())));
                item.add(new Label("address", of(detail.displayAddress(getLocale()))));
                item.add(new Label("ownerInn", of(detail.getOwnerINN())));
                item.add(new Label("ercCode", of(detail.getErcCode())));

                WebMarkupContainer lodgersContainer = new WebMarkupContainer("lodgersContainer");
                lodgersContainer.setOutputMarkupPlaceholderTag(true);
                lodgersContainer.setOutputMarkupId(true);
                lodgersContainer.setVisible(false);
                item.add(lodgersContainer);

                ListModel<Lodger> lodgerModel = new ListModel<>();

                FeedbackPanel feedbackPanel = new FeedbackPanel("messages", new ContainerFeedbackMessageFilter(lodgersContainer));
                lodgersContainer.add(feedbackPanel);

                lodgersContainer.add(new ListView<Lodger>("lodgers", lodgerModel) {
                    @Override
                    protected void populateItem(ListItem<Lodger> i) {
                        Lodger lodger = i.getModelObject();

                        i.add(new Label("fio", Model.of(lodger.getFio())));
                        i.add(new Label("birthDate", Model.of(lodger.getBirthDate())));
                        i.add(new Label("passport", Model.of(lodger.getPassport())));
                        i.add(new Label("idCode", Model.of(lodger.getIdCode())));
                        i.add(new Label("dateIn", Model.of(lodger.getDateIn())));
                        i.add(new Label("dateOut", Model.of(lodger.getDateOut())));
                    }
                });

                item.add(new AjaxLink("lodgers") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            Cursor<Lodger> cursor =  serviceProviderAdapter.getLodgers(userOrganizationId.getObject(),
                                    detail.getAccCode(), dateModel.getObject());

                            if (cursor.getResultCode() == -1){
                                lodgersContainer.error(getString("error_acc_code_not_found"));
                            }else if (cursor.getResultCode() == -10){
                                lodgersContainer.error(getString("error_error"));
                            }else if (cursor.getData() != null){
                                lodgerModel.setObject(cursor.getData());
                            }
                        } catch (Exception e) {
                            lodgersContainer.error(ExceptionUtil.getCauseMessage(e));
                        }

                        lodgersContainer.setVisible(!lodgersContainer.isVisible());

                        target.add(lodgersContainer);
                    }
                });
            }
        };
        accountDetails.setRenderBodyOnly(true);
        radioGroup.add(accountDetails);

        add(new ArrowOrderByBorder("accCodeHeader", "accCode", dataProvider, accountDetails, this));
        add(new ArrowOrderByBorder("zheuHeader", "zheu", dataProvider, accountDetails, this));
        add(new ArrowOrderByBorder("zheuCodeHeader", "zheuCode", dataProvider, accountDetails, this));
        add(new ArrowOrderByBorder("ownerFioHeader", "ownerFio", dataProvider, accountDetails, this));
        add(new ArrowOrderByBorder("addressHeader", "address", dataProvider, accountDetails, this));
        add(new ArrowOrderByBorder("ownerInnHeader", "ownerInn", dataProvider, accountDetails, this));
        add(new ArrowOrderByBorder("ercCodeHeader", "ercCode", dataProvider, accountDetails, this));

        radioGroup.add(new PagingNavigator("navigator", accountDetails, this));
    }
}
