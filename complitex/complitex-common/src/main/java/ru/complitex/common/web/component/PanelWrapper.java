package ru.complitex.common.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * @author inheaven on 001 01.04.15 19:12
 */
public class PanelWrapper extends Panel implements IMarkupResourceStreamProvider {
    public final static String ID = "componentId";

    public enum TYPE{INPUT_CHECKBOX, INPUT_TEXT, SELECT}

    private TYPE type;

    public static PanelWrapper of(String id, Component component, TYPE type){
        return new PanelWrapper(id, component, type);
    }

    public PanelWrapper(String id, Component component, TYPE type) {
        super(id);

        this.type = type;

        add(component);
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        String markup = "";

        switch (type){
            case INPUT_CHECKBOX:
                markup = "<wicket:panel><input type=\"checkbox\" wicket:id=\"" + ID + "\"/></wicket:panel>";
                break;
            case INPUT_TEXT:
                markup = "<wicket:panel><input type=\"text\" wicket:id=\"" + ID + "\"/></wicket:panel>";
                break;
            case SELECT:
                markup = "<wicket:panel><select wicket:id=\"" + ID + "\"></select></wicket:panel>";
                break;

        }

        return new StringResourceStream(markup);
    }
}
