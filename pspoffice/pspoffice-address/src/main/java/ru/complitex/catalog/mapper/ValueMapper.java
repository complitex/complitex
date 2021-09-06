package ru.complitex.catalog.mapper;

import org.apache.ibatis.annotations.*;
import org.mybatis.cdi.Mapper;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Type;
import ru.complitex.catalog.entity.Value;

import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
@Mapper
@CacheNamespace
public interface ValueMapper {
    @Select("SELECT * FROM type WHERE id = #{typeId} AND \"end\" IS NULL")
    Type selectType(Long typeId);

    @Select("SELECT * FROM locale WHERE id = #{localeId} AND \"end\" IS NULL")
    Locale selectLocale(Long localeId);

        @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "type", column = "type_id", one = @One(select = "selectType")),
            @Result(property = "locale",  column = "locale_id", one = @One(select = "selectLocale")),
            @Result(property = "referenceCatalog", column = "reference_catalog_id",
                    one = @One(select = "ru.complitex.catalog.mapper.CatalogMapper.selectCatalog"))
    })
    @Select("SELECT * FROM value_relevance(current_date) WHERE id = #{id}")
    Value selectValue(Long id);

    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "type", column = "type_id", one = @One(select = "selectType")),
            @Result(property = "locale",  column = "locale_id", one = @One(select = "selectLocale")),
            @Result(property = "referenceCatalog", column = "reference_catalog_id",
                    one = @One(select = "ru.complitex.catalog.mapper.CatalogMapper.selectCatalog"))
    })
    @Select("SELECT * FROM value_relevance(current_date) WHERE catalog_id = #{catalogId} order by key_id asc, locale_id asc")
    List<Value> selectValues(Long catalogId);
}
