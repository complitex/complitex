package org.complitex.common.service;

import org.complitex.common.entity.Name;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.02.11 14:53
 */
@Stateless
public class NameBean extends AbstractBean{
    public final static String NS = NameBean.class.getName();

    /*select names by filter*/

    @SuppressWarnings({"unchecked"})
    public List<String> getFirstNames(final String filter, final int size){
        return sqlSession().selectList(NS + ".selectFirstNames",
                new HashMap(){{
                    put("filter", filter);
                    put("size", size);
                }});
    }

    @SuppressWarnings({"unchecked"})
    public List<String> getMiddleNames(final String filter, final int size){
        return sqlSession().selectList(NS + ".selectMiddleNames",
                new HashMap(){{
                    put("filter", filter);
                    put("size", size);
                }});
    }

    @SuppressWarnings({"unchecked"})
    public List<String> getLastNames(final String filter, final int size){
        return sqlSession().selectList(NS + ".selectLastNames",
                new HashMap(){{
                    put("filter", filter);
                    put("size", size);
                }});
    }

    /*select name by id*/

    public String getFirstName(Long id){
        return (String) sqlSession().selectOne(NS + ".selectFirstName", id);
    }

    public String getMiddleName(Long id){
        return (String) sqlSession().selectOne(NS + ".selectMiddleName", id);
    }

    public String getLastName(Long id){
        return (String) sqlSession().selectOne(NS + ".selectLastName", id);
    }

    /*select id by name*/


    public Long getFirstNameId(String firstName, boolean createIfNotExist){
        Long id = (Long) sqlSession().selectOne(NS + ".selectFirstNameId", firstName);

        if (id == null && createIfNotExist){
            id = saveFirstName(firstName);
        }

        return id;
    }


    public Long getMiddleNameId(String middleName, boolean createIfNotExist){
        Long id = (Long) sqlSession().selectOne(NS + ".selectMiddleNameId", middleName);

        if (id == null && createIfNotExist){
            id = saveMiddleName(middleName);
        }

        return id;
    }


    public Long getLastNameId(String lastName, boolean createIfNotExist){
        Long id = (Long) sqlSession().selectOne(NS + ".selectLastNameId", lastName);

        if (id == null && createIfNotExist){
            id = saveLastName(lastName);
        }

        return id;
    }

    /*save name*/


    public Long saveFirstName(String firstName){
        Name name = new Name(formatCase(firstName));

        sqlSession().insert(NS + ".insertFirstName", name);

        return name.getId();
    }


    public Long saveMiddleName(String middleName){
        Name name = new Name(formatCase(middleName));

        sqlSession().insert(NS + ".insertMiddleName", name);

        return name.getId();
    }


    public Long  saveLastName(String lastName){
        Name name = new Name(formatCase(lastName));

        sqlSession().insert(NS + ".insertLastName", name);

        return name.getId();
    }

    private String formatCase(String s){
        if (s == null || s.length() < 1 || s.indexOf('-') > -1){
            return s;
        }

        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
