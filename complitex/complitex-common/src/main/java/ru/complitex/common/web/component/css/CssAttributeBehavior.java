package ru.complitex.common.web.component.css;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.util.string.Strings;

import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 *
 * @author Artem
 */
public class CssAttributeBehavior extends Behavior {

    private Set<String> cssClasses = newLinkedHashSet();

    public CssAttributeBehavior(String cssClass) {
        cssClasses.add(cssClass);
    }

    public CssAttributeBehavior(Set<String> cssClasses) {
        this.cssClasses.addAll(cssClasses);
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        for (String className : cssClasses) {
            if (!Strings.isEmpty(className)) {
                CharSequence oldClassName = tag.getAttribute("class");
                if (Strings.isEmpty(oldClassName)) {
                    tag.put("class", className);
                } else {
                    tag.put("class", oldClassName + " " + className);
                }
            }
        }
    }
}
