package ru.complitex.catalog.mapper;

import org.apache.ibatis.annotations.*;
import org.mybatis.cdi.Mapper;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.mapper.provider.ItemProvider;
import ru.complitex.ui.entity.Filter;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
@Mapper
public interface ItemMapper {
    @Results({
            @Result(property = "catalog", column = "catalog_id",
                    one = @One(select = "ru.complitex.catalog.mapper.CatalogMapper.selectCatalog")),
            @Result(property = "data", column = "{table=table_name,itemId=id,date=date}",
                    many = @Many(select = "ru.complitex.catalog.mapper.DataMapper.selectData"))
    })
    @Select("SELECT *, '${table}' AS table_name, #{date} AS date FROM ${table} WHERE id = #{id} AND \"end\" IS NULL")
    Item selectItem(@Param("table") String table, @Param("id") Long id, @Param("date") LocalDate date);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO ${catalog.table} (catalog_id) VALUES (#{catalog.id})")
    void insertItem(Item item);

    @SelectProvider(type = ItemProvider.class, method = "selectItemsCount")
    Long selectItemsCount(@Param("filter") Filter<Item> filter);

    @Results({
            @Result(property = "catalog", column = "catalog_id",
                    one = @One(select = "ru.complitex.catalog.mapper.CatalogMapper.selectCatalog")),
            @Result(property = "data", column = "{table=table_name,itemId=id,date=date}",
                    many = @Many(select = "ru.complitex.catalog.mapper.DataMapper.selectData"))
    })
    @SelectProvider(value = ItemProvider.class, method = "selectItems")
    List<Item> selectItems(@Param("filter") Filter<Item> filter);
}
