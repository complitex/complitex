package org.complitex.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 18:21
 */
public class FixedIdBaseTypeHandler<T extends Enum & IFixedIdType> implements TypeHandler<T> {
    private Class<T> _class;
    private final T[] enums;

    public FixedIdBaseTypeHandler(Class<T> _class) {
        this._class = _class;
        this.enums = _class.getEnumConstants();

        if (this.enums == null) {
            throw new IllegalArgumentException(_class.getSimpleName() + " does not represent an enum type.");
        }
    }

    private T getType(Integer id){
        for (T type : enums){
            if (id.equals(type.getId())){
                return type;
            }
        }

        throw new IllegalArgumentException("Cannot convert " + id + " to " + _class.getSimpleName());
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            ps.setInt(i, parameter.getId());
        }else {
            ps.setNull(i, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        Integer i = rs.getInt(columnName);

        return !rs.wasNull() ? getType(i) : null;
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer i = rs.getInt(columnIndex);

        return !rs.wasNull() ? getType(i) : null;
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer i = cs.getInt(columnIndex);

        return !cs.wasNull() ? getType(i) : null;
    }
}
