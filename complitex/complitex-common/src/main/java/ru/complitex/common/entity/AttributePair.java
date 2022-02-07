package ru.complitex.common.entity;

/**
 * @author inheaven on 018 18.05.15 17:40
 */
public class AttributePair {
    private Attribute left;
    private Attribute right;

    public AttributePair(Attribute left, Attribute right) {
        this.left = left;
        this.right = right;
    }

    public Attribute getLeft() {
        return left;
    }

    public Attribute getRight() {
        return right;
    }
}
