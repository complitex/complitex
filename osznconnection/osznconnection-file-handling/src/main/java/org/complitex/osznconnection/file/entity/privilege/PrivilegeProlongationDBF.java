package org.complitex.osznconnection.file.entity.privilege;

/**
 * @author inheaven on 23.06.2016.
 */
public enum PrivilegeProlongationDBF {
    COD, //Код ОСЗН
    CDPR, //ЕДРПОУ код предприятия
    NCARD, //Номер дела в ОСЗН
    IDPIL, //Иден.код льготника
    PASPPIL, //Паспорт льготника
    FIOPIL, //ФИО льготника
    INDEX, //Почтовый индекс
    CDUL, //Код улицы
    HOUSE, //Номер дома
    BUILD, //Номер корпуса
    APT, //Номер квартиры
    KAT, //Код льготы ЕДАРП
    LGCODE, //Код услуги (500 - квп, 5061|5062 - мусор)
    DATEIN, //Дата начала действия льготы
    DATEOUT, //Дата окончания действия льготы
    RAH //Номер л/с ПУ
}
