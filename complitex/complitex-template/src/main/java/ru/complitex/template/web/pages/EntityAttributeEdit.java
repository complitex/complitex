package ru.complitex.template.web.pages;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.complitex.pspoffice.commons.web.pages;
//
//import com.google.common.collect.ImmutableMap;
//import org.apache.wicket.PageParameters;
//import ru.complitex.dictionary.strategy.web.EntityAttributeEditPanel;
//import ru.complitex.pspoffice.commons.web.template.FormTemplatePage;
//
///**
// *
// * @author Artem
// */
//public final class EntityAttributeEdit extends FormTemplatePage {
//
//    public static final String ENTITY = "entity";
//
//    public EntityAttributeEdit(PageParameters params) {
//        String entity = params.getString(ENTITY);
//        PageParameters entityDescriptionPageParams = new PageParameters(ImmutableMap.of(EntityDescription.ENTITY, entity));
//        add(new EntityAttributeEditPanel("entityAttributeEditPanel", entity, EntityDescription.class, entityDescriptionPageParams));
//    }
//}
//
