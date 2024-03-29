package ru.complitex.salelog.entity;

import ru.complitex.common.entity.DictionaryObject;

import java.math.BigDecimal;

/**
 * @author Pavel Sknar
 */
public class Product extends DictionaryObject {

    private String code;
    private String name;
    private BigDecimal price;

    public Product() {
    }

    public Product(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
