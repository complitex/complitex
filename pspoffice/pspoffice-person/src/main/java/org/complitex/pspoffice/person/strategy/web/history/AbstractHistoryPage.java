package org.complitex.pspoffice.person.strategy.web.history;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.template.web.template.TemplatePage;
import org.odlabs.wiquery.core.javascript.JsQuery;

import java.util.Date;

/**
 *
 * @author Artem
 */
public abstract class AbstractHistoryPage extends TemplatePage {

    private static final String HISTORY_CONTENT_WICKET_ID = "historyContent";
    private final WebMarkupContainer historyContainer;
    private final long objectId;
    private Date currentEndDate;
    private final IModel<String> objectLinkMessageModel;

    private abstract class HistoryLink extends AjaxLink<Void> {

        private boolean isPostBack;
        private final boolean initiallyVisible;

        private HistoryLink(String id, boolean initiallyVisible) {
            super(id);
            this.initiallyVisible = initiallyVisible;
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);

            attributes.getAjaxCallListeners().add(new AjaxCallListener(){
                @Override
                public AjaxCallListener onBefore(CharSequence before) {
                    return super.onBefore("(function(){$(this).attr('disabled', true); $('#load_indicator').show();})();" + before);
                }
            });
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            historyContainer.replace(newHistoryContent(HISTORY_CONTENT_WICKET_ID, objectId, currentEndDate));
            target.add(historyContainer);
            setHistoryButtonsVisibility(target);
        }

        protected final void postOnClick(AjaxRequestTarget target) {
            String enableButtonScript = new JsQuery(this).$().render(false) + ".attr('disabled', false);";
            target.appendJavaScript("(function(){" + enableButtonScript + "$('#load_indicator').hide()})();");
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (!isPostBack) {
                isPostBack = true;
                if (!initiallyVisible) {
                    String style = "";
                    String styleCS = tag.getAttribute("style");
                    if (!Strings.isEmpty(styleCS)) {
                        style = styleCS.toString();
                    }
                    if (!Strings.isEmpty(style) && !style.endsWith(";")) {
                        style += ";";
                    }
                    tag.put("style", style + "visibility: hidden;");
                }
            }
        }
    }

    public AbstractHistoryPage(final long objectId, final IModel<String> titleModel,
            final IModel<String> objectLinkMessageModel) {
        this.objectId = objectId;
        this.objectLinkMessageModel = objectLinkMessageModel;

        add(new Label("title", titleModel));

        historyContainer = new WebMarkupContainer("historyContainer");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(
                AbstractHistoryPage.class, AbstractHistoryPage.class.getSimpleName() + ".css")));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        //history container and content.
        historyContainer.setOutputMarkupId(true);
        historyContainer.add(newHistoryContent(HISTORY_CONTENT_WICKET_ID, objectId, currentEndDate));
        add(historyContainer);

        //history buttons
        boolean backButtonVisibleInitially = isBackButtonVisible();
        boolean forwardButtonVisibleInitially = isForwardButtonVisible();

        WebMarkupContainer historyButtonsContainer = new WebMarkupContainer("historyButtonsContainer");
        historyButtonsContainer.setVisible(backButtonVisibleInitially || forwardButtonVisibleInitially);
        add(historyButtonsContainer);

        historyButtonsContainer.add(new HistoryLink("back", backButtonVisibleInitially) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Date newEndDate = getPreviousModificationDate(objectId, currentEndDate);
                if (getPreviousModificationDate(objectId, newEndDate) != null) {
                    currentEndDate = newEndDate;
                    super.onClick(target);
                }
                postOnClick(target);
            }
        });

        historyButtonsContainer.add(new HistoryLink("forward", forwardButtonVisibleInitially) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (currentEndDate != null) {
                    currentEndDate = getNextModificationDate(objectId, currentEndDate);
                    super.onClick(target);
                }
                postOnClick(target);
            }
        });

        Link<Void> objectLink = new Link<Void>("objectLink") {

            @Override
            public void onClick() {
                returnBackToObject(objectId);
            }
        };
        objectLink.add(new Label("objectLinkMessage", objectLinkMessageModel));
        add(objectLink);
    }

    protected abstract void returnBackToObject(long objectId);

    protected abstract Date getPreviousModificationDate(long objectId, Date currentEndDate);

    protected abstract Date getNextModificationDate(long objectId, Date currentEndDate);

    private boolean isBackButtonVisible() {
        return getPreviousModificationDate(objectId,
                getPreviousModificationDate(objectId, currentEndDate)) != null;
    }

    private boolean isForwardButtonVisible() {
        return currentEndDate != null;
    }

    private void setHistoryButtonsVisibility(AjaxRequestTarget target) {
        target.appendJavaScript("(function(){ $('.history_back_button').css('visibility', '"
                + getCssVisibility(isBackButtonVisible()) + "');"
                + "$('.history_forward_button').css('visibility', '" + getCssVisibility(isForwardButtonVisible()) + "'); })()");
    }

    private String getCssVisibility(boolean visible) {
        return visible ? "visible" : "hidden";
    }

    protected abstract Component newHistoryContent(String id, long objectId, Date currentEndDate);
}
