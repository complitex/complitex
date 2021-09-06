package ru.complitex.catalog.mapper;

import org.apache.ibatis.annotations.*;
import org.mybatis.cdi.Mapper;
import ru.complitex.catalog.entity.Catalog;

/**
 * @author Ivanov Anatoliy
 */
@Mapper
@CacheNamespace
public interface CatalogMapper {
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "values", column = "id",
                    many = @Many(select = "ru.complitex.catalog.mapper.ValueMapper.selectValues"))
    })
    @Select("SELECT * FROM catalog_view WHERE id = #{id}")
    Catalog selectCatalog(Long id);

    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "values", column = "id",
                    many = @Many(select = "ru.complitex.catalog.mapper.ValueMapper.selectValues"))
    })
    @Select("SELECT * FROM catalog_view WHERE key_id = #{keyId}")
    Catalog selectCatalogByKeyId(int keyId);
}
