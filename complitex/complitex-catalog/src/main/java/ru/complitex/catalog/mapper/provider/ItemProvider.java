package ru.complitex.catalog.mapper.provider;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import ru.complitex.catalog.entity.Data;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Type;
import ru.complitex.catalog.entity.Value;
import ru.complitex.ui.entity.Filter;

import java.util.List;
import java.util.Set;

/**
 * @author Ivanov Anatoliy
 */
public class ItemProvider {
    private static String selectItemsSql(Filter<Item> filter, boolean selectCount) {
        Item item = filter.getObject();

        String table = item.getCatalog().getTable();

        SQL sql = new SQL();

        if (selectCount) {
            sql.SELECT("count(DISTINCT " + table + ".*)");
        } else {
            sql.SELECT_DISTINCT(table + ".*, '" + table + "' AS table_name, #{filter.date} AS date");
        }

        sql.FROM(table);

        sql.WHERE(table + ".\"end\" IS NULL");

        Set<?> noId = (Set<?>) filter.getMap().get("noId");

        if (noId != null) {
            noId.forEach(id -> {
                if (id != null) {
                    sql.AND();
                    sql.WHERE(table + ".id <> " + id);
                }
            });
        }

        List<Data> itemData = item.getData();

        for (int i = 0; i < itemData.size(); i++) {
            Data data = itemData.get(i);

            Value value = data.getValue();

            if (filter.isLike()) {
                if (data.isEmpty()) {
                    continue;
                }

                if (data.getText() != null && data.getText().matches("\\s*")) {
                    sql.AND();

                    sql.WHERE("NOT EXISTS (SELECT id FROM " + table + "_data data " +
                            "WHERE " + table + ".id = " + table + "_id AND data.value_id = #{filter.object.data[" + i + "].value.id} " +
                            "AND data.\"end\" IS NULL " +
                            "AND (data.start_date IS NULL OR data.start_date <= #{filter.date}) " +
                            "AND (data.end_date IS NULL OR data.end_date > #{filter.date}))");

                    continue;
                }
            }

            String relevance = "data_" + value.getId();

            sql.LEFT_OUTER_JOIN(
                    table + "_data " + relevance + " ON " + table + ".id = " + relevance + "." + table + "_id  " +
                            "AND " + relevance + ".value_id = #{filter.object.data[" + i + "].value.id}"
            );

            sql.AND();

            sql.WHERE(relevance + ".\"end\" IS NULL");

            if (filter.getDate() != null) {
                sql.AND();

                sql.WHERE("(" + relevance + ".start_date IS NULL OR " + relevance + ".start_date <= #{filter.date}) " +
                        "AND (" + relevance + ".end_date IS NULL OR " + relevance + ".end_date > #{filter.date})");
            }

            if (filter.isAnd()) {
                sql.AND();
            } else if (filter.isOr()) {
                sql.OR();
            }

            if (value.getType().getKeyId() == Type.REFERENCE) {
                if (data.getReferenceId() != null) {
                    sql.WHERE(relevance + ".reference_id = #{filter.object.data[" + i + "].referenceId}");
                } else if (filter.isEqual()) {
                    sql.WHERE(relevance + ".reference_id IS NULL");
                } else if (filter.isLike() && data.getText() != null) {
                    String reference = value.getReferenceCatalog().getTable();

                    sql.LEFT_OUTER_JOIN(
                            reference + "_data " + relevance + "_reference ON " + relevance + ".reference_id = " +
                                    relevance + "_reference." + reference + "_id"
                    );

                    sql.WHERE(relevance + "_reference.text like concat('%', '" + data.getText().toUpperCase() + "', '%')");
                }
            }

            if (value.getType().getKeyId() == Type.TEXT) {
                if (filter.isEqual()) {
                    if (data.getText() != null) {
                        sql.WHERE(relevance + ".text = #{filter.object.data[" + i + "].text}");
                    } else {
                        sql.WHERE(relevance + ".text IS NULL");
                    }
                } else if (filter.isLike()) {
                    sql.WHERE(relevance + ".text like concat('%', '" + data.getText().toUpperCase() + "', '%')");
                }
            }

            if (value.getType().getKeyId() == Type.NUMERIC) {
                if (data.getNumeric() != null) {
                    Set<?> notNull = (Set<?>) filter.getMap().get("notNull");

                    if (notNull != null && notNull.contains(value.getKeyId())) {
                        sql.WHERE(relevance + ".numeric IS NOT NULL");
                    } else {
                        sql.WHERE(relevance + ".numeric = #{filter.object.data[" + i + "].numeric}");
                    }
                } else if (filter.isEqual()) {
                    sql.WHERE(relevance + ".numeric IS NULL");
                } else if (filter.isLike() && data.getText() != null) {
                    sql.WHERE(relevance + ".numeric::text like concat('%', '" + data.getText().toUpperCase() + "', '%')");
                }
            }

            if (value.getType().getKeyId() == Type.TIMESTAMP) {
                if (filter.isEqual()) {
                    sql.WHERE(relevance + ".timestamp = #{filter.object.data[" + i + "].timestamp}");
                } else if (filter.isLike()) {
                    sql.WHERE("date(" + relevance + ".timestamp)::text like concat('%', '" + data.getText().toUpperCase() + "', '%')");
                }
            }
        }

        if (!selectCount) {
            if (filter.getOffset() != null) {
                sql.OFFSET(filter.getOffset());
            }

            if (filter.getLimit() != null) {
                sql.LIMIT(filter.getLimit().intValue());
            }

            sql.ORDER_BY("id");
        }

        return sql.toString();
    }

    public static String selectItemsCount(@Param("filter") Filter<Item> filter) {
        return selectItemsSql(filter, true);
    }

    public static String selectItems(@Param("filter") Filter<Item> filter) {
        return selectItemsSql(filter, false);
    }
}
