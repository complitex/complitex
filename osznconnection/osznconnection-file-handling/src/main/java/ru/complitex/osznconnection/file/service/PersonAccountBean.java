package ru.complitex.osznconnection.file.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.service.SessionBean;
import ru.complitex.osznconnection.file.entity.PersonAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Класс для работы с локальной таблицей номеров л/c person_account.
 */
@Stateless
public class PersonAccountBean extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(PersonAccountBean.class);

    private static final String NS = PersonAccountBean.class.getName();

    @EJB
    private SessionBean sessionBean;


    public PersonAccount getPersonAccount(Long id) {
        return sqlSession().selectOne(NS + ".selectPersonAccount", id);
    }

    public List<PersonAccount> getPersonAccounts(FilterWrapper<PersonAccount> filterWrapper){
        return sqlSession().selectList(NS + ".selectPersonAccounts", filterWrapper);
    }

    public Long getPersonAccountsCount(FilterWrapper<PersonAccount> filterWrapper){
        return sqlSession().selectOne(NS + ".selectPersonAccountsCount", filterWrapper);
    }

    public void save(PersonAccount personAccount) {
        if(personAccount.getId() == null){
            sqlSession().insert(NS + ".insertPersonAccount", personAccount);
        }else{
            sqlSession().update(NS + ".updatePersonAccount", personAccount);
        }
    }

    public void delete(PersonAccount personAccount) {
        sqlSession().delete(NS + ".delete", personAccount);
    }

}
