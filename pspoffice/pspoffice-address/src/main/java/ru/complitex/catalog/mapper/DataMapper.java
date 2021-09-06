package ru.complitex.catalog.mapper;

import org.apache.ibatis.annotations.*;
import org.mybatis.cdi.Mapper;
import ru.complitex.catalog.entity.Data;
import ru.complitex.catalog.entity.Item;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
@Mapper
public interface DataMapper {
    @Result(property = "value", column = "value_id",
            one = @One(select = "ru.complitex.catalog.mapper.ValueMapper.selectValue"))
    @Select("SELECT * FROM ${table}_data " +
            "WHERE ${table}_id = #{itemId} " +
            "AND \"end\" IS NULL " +
            "AND (#{date}::date IS NULL " +
            "OR ((start_date IS NULL OR start_date <= #{date}) " +
            "AND (end_date IS NULL OR end_date > #{date})))")
    List<Data> selectData(@Param("table") String table, @Param("itemId") Long itemId, @Param("date") LocalDate date);

    @Result(property = "value", column = "value_id",
            one = @One(select = "ru.complitex.catalog.mapper.ValueMapper.selectValue"))
    @Select("SELECT * FROM ${item.catalog.table}_data " +
            "WHERE ${item.catalog.table}_id = #{item.id} AND value_id = #{data.value.id} " +
            "AND \"end\" IS NULL " +
            "AND (start_date IS NOT NULL AND start_date > #{date}) " +
            "ORDER BY start_date LIMIT 1")
    Data selectDataAfter(@Param("item") Item item, @Param("data") Data data, @Param("date") LocalDate date);

    @Options(useGeneratedKeys = true, keyProperty = "data.id", keyColumn = "id")
    @Insert("INSERT INTO ${item.catalog.table}_data " +
            "(${item.catalog.table}_id, value_id, index, numeric, text, timestamp, reference_id, start_date, end_date) " +
            "VALUES (#{item.id}, #{data.value.id}, #{data.index}, #{data.numeric}, #{data.text}, #{data.timestamp}, #{data.referenceId}, " +
            "#{data.startDate}, #{data.endDate})")
    void insertData(@Param("item") Item item, @Param("data") Data data);

    @Insert("<script>" +
            "INSERT INTO ${item.catalog.table}_data " +
            "(${item.catalog.table}_id, value_id, index, numeric, text, timestamp, reference_id, start_date, end_date) " +
            "VALUES " +
            "<foreach item='d' collection='data' separator=','>" +
            "(#{item.id}, #{d.value.id}, #{d.index}, #{d.numeric}, #{d.text}, #{d.timestamp}, #{d.referenceId}, #{d.startDate}, #{d.endDate}) " +
            "</foreach>" +
            "</script>")
    void insertsData(@Param("item") Item item, @Param("data") List<Data> data);

    @Update("UPDATE ${item.catalog.table}_data SET end_date = #{data.endDate} " +
            "WHERE ${item.catalog.table}_id = #{item.id} AND id = #{data.id}")
    void updateData(@Param("item") Item item, @Param("data") Data data);

    @Update("UPDATE ${item.catalog.table}_data SET \"end\" = current_timestamp " +
            "WHERE ${item.catalog.table}_id = #{item.id} AND id = #{data.id}")
    void deleteData(@Param("item") Item item, @Param("data") Data data);
}
