package org.complitex.template.web.template;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 18:38:32
 *
 * Загружает список меню из файла конфигурации.
 */
public class TemplateLoader {
    private static final String SIDEBAR_ELEMENT_NAME = "sidebar";

    private static final String MENU_ELEMENT_NAME = "menu";
    private static final String MENU_ATTRIBUTE_CLASS = "class";

    private static final String GROUPS_ELEMENT_NAME = "groups";
    private static final String GROUP_ELEMENT_NAME = "group";
    private static final String GROUP_ATTRIBUTE_NAME = "name";

    private final List<String> menuClassNames;
    private final List<String> groupNames;

    private final String homePageClassName;
    private final String mainUserOrganizationPickerComponentClassName;
    private final String domainObjectPermissionPanelClassName;
    private final String organizationPermissionPanelClassName;
    private final String userOrganizationPickerClassName;

    public TemplateLoader(InputStream inputStream) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);

            menuClassNames = getMenuClassNames(document);

            groupNames = getGroupNames(document);

            XPath xpath = XPathFactory.newInstance().newXPath();
            homePageClassName = getHomePageClassName(xpath, document);
            mainUserOrganizationPickerComponentClassName =
                    getMainUserOrganizationPickerComponentClassName(xpath, document);
            domainObjectPermissionPanelClassName = getDomainObjectPermissionPanelClassName(xpath, document);
            organizationPermissionPanelClassName = getOrganizationPermissionPanelClassName(xpath, document);
            userOrganizationPickerClassName = getUserOrganizationPickerClassName(xpath, document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getMenuClassNames(Document document) {
        NodeList sidebar = document.getElementsByTagName(SIDEBAR_ELEMENT_NAME);

        if (sidebar.getLength() == 0) {
            return null;
        }

        List<String> menuClassNamesList = new ArrayList<>();

        NodeList fragments = sidebar.item(0).getChildNodes();
        for (int i = 0; i < fragments.getLength(); i++) {
            if (fragments.item(i) instanceof Element) {
                Element menu = (Element) fragments.item(i);
                if (MENU_ELEMENT_NAME.equals(menu.getTagName())) {
                    String className = menu.getAttribute(MENU_ATTRIBUTE_CLASS);
                    if (className.length() > 0) {
                        menuClassNamesList.add(className);
                    }
                }
            }
        }

        return menuClassNamesList;
    }

    private List<String> getGroupNames(Document document) {
        NodeList groups = document.getElementsByTagName(GROUPS_ELEMENT_NAME);

        if (groups.getLength() == 0) {
            return null;
        }

        List<String> list = new ArrayList<String>();

        NodeList fragments = groups.item(0).getChildNodes();

        for (int i = 0; i < fragments.getLength(); i++) {
            if (fragments.item(i) instanceof Element) {
                Element group = (Element) fragments.item(i);
                if (GROUP_ELEMENT_NAME.equals(group.getTagName())) {
                    String name = group.getAttribute(GROUP_ATTRIBUTE_NAME);

                    if (!name.isEmpty()) {
                        list.add(name);
                    }
                }
            }
        }

        return list;
    }

    private String getHomePageClassName(XPath xpath, Document doc) {
        return getPathValue("//homepage-class/text()", xpath, doc);
    }

    private String getMainUserOrganizationPickerComponentClassName(XPath xpath, Document doc) {
        return getPathValue("//web-components/main-user-organization-picker-component/text()", xpath, doc);
    }

    private String getDomainObjectPermissionPanelClassName(XPath xpath, Document doc) {
        return getPathValue("//web-components/domain-object-permission-panel/text()", xpath, doc);
    }

    private String getOrganizationPermissionPanelClassName(XPath xpath, Document doc) {
        return getPathValue("//web-components/organization-permission-panel/text()", xpath, doc);
    }

    private String getUserOrganizationPickerClassName(XPath xpath, Document doc) {
        return getPathValue("//web-components/user-organization-picker/text()", xpath, doc);
    }

    private String getPathValue(String expression, XPath xpath, Document doc) {
        try {
            Node text = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
            if (text != null) {
                return text.getNodeValue().trim();
            }
            return null;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getMenuElements() {
        return menuClassNames;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public String getHomePageClassName() {
        return homePageClassName;
    }

    public String getMainUserOrganizationPickerClassName() {
        return mainUserOrganizationPickerComponentClassName;
    }

    public String getDomainObjectPermissionPanelClassName() {
        return domainObjectPermissionPanelClassName;
    }

    public String getOrganizationPermissionPanelClassName() {
        return organizationPermissionPanelClassName;
    }

    public String getUserOrganizationPickerClassName() {
        return userOrganizationPickerClassName;
    }
}
