package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.osznconnection.file.entity.subsidy.SubsidySplit;
import org.complitex.osznconnection.file.entity.subsidy.SubsidySplitField;
import org.complitex.osznconnection.file.service.subsidy.SubsidySplitBean;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.01.14 18:37
 */
@AuthorizeInstantiation("SUBSIDY_FILE")
public class SubsidySplitList extends TemplatePage {
    @EJB
    private SubsidySplitBean subsidySplitBean;

    public SubsidySplitList(PageParameters pageParameters) {
        Long subsidyId = pageParameters.get("subsidy_id").toLongObject();
        final Long requestFileId = pageParameters.get("request_file_id").toLongObject();

        add(new Label("title", new ResourceModel("title")));
        add(new Label("label", new ResourceModel("title")));

        List<IColumn<SubsidySplit, String>> columns = new ArrayList<>();

        for (final SubsidySplitField key : SubsidySplitField.values()){
            columns.add(new AbstractColumn<SubsidySplit, String>(Model.of(key.name())) {
                @Override
                public void populateItem(Item<ICellPopulator<SubsidySplit>> cellItem, String componentId,
                                         IModel<SubsidySplit> rowModel) {
                    cellItem.add(new Label(componentId, new PropertyModel<>(rowModel, "fieldMap." + key.name())));
                }
            });
        }

        DataTable<SubsidySplit, String> dataTable = new DataTable<>("data_table", columns,
                new ListDataProvider<>(subsidySplitBean.getSubsidySplits(subsidyId)), 10);
        dataTable.addTopToolbar(new HeadersToolbar<>(dataTable, null));
        add(dataTable);

        add(new AjaxLink("back") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SubsidyList.class, new PageParameters().add("request_file_id", requestFileId));
            }
        });
    }
}
