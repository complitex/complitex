package ru.complitex.common.web.component.datatable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author inheaven on 031 31.03.15 17:56
 */
public class FilteredActionHeader extends Panel {
    public FilteredActionHeader(String id) {
        super(id);

        add(new AjaxLink("reset") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                FilteredActionHeader.this.onClick(target);
            }
        });
    }

    protected void onClick(AjaxRequestTarget target){
    }
}
