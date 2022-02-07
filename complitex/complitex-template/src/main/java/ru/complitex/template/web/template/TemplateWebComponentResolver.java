package ru.complitex.template.web.template;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.Component;
import ru.complitex.common.web.IWebComponentResolver;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Artem
 */
public class TemplateWebComponentResolver implements IWebComponentResolver {

    public static class TemplateWebComponentResolverBuilder {

        private final Map<String, Class<? extends Component>> componentMapping = new HashMap<>();

        public TemplateWebComponentResolverBuilder addComponentMapping(String componentName,
                Class<? extends Component> componentClass) {
            componentMapping.put(componentName, componentClass);
            return this;
        }

        public TemplateWebComponentResolver build() {
            return new TemplateWebComponentResolver(componentMapping);
        }
    }
    private final Map<String, Class<? extends Component>> componentMapping;

    private TemplateWebComponentResolver(Map<String, Class<? extends Component>> componentMapping) {
        this.componentMapping = ImmutableMap.copyOf(componentMapping);
    }

    @Override
    public Class<? extends Component> getComponentClass(String componentName) {
        return componentMapping.get(componentName);
    }
}
