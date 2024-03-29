package ru.complitex.common.util;

/**
 *
 * @author Artem
 */
public final class Numbers {

    private Numbers() {
    }

    public static boolean isEqual(Long n1, Long n2) {
        if ((n1 == null) && (n2 == null)) {
            return true;
        }

        if (n1 == null || n2 == null) {
            return false;
        }

        return n1.equals(n2);
    }
}
