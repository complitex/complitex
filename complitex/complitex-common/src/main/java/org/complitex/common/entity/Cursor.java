package org.complitex.common.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly Ivanov
 *         Date: 10.06.14 18:41
 */
public class Cursor<T> {
    private String dataSource;
    private Integer resultCode;
    private List<T> data = new ArrayList<>();

    public Cursor() {
    }

    public Cursor(Integer resultCode, List<T> data) {
        this.resultCode = resultCode;
        this.data = data;
    }

    public boolean isEmpty(){
        return data == null || data.isEmpty();
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Cursor{" +
                "resultCode=" + resultCode +
                ", data=" + data +
                '}';
    }
}
