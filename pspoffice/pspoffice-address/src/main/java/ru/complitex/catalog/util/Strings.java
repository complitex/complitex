package ru.complitex.catalog.util;

import ru.complitex.catalog.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class Strings {
    public static String toString(Object object) {
        Class<?> objectClass = object.getClass();

        StringBuilder stringBuilder = new StringBuilder()
                .append("{\"class\": \"")
                .append(objectClass.getSimpleName())
                .append( "\"");

        string(objectClass, object, stringBuilder);

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private static void string(Class<?> objectClass, Object object, StringBuilder stringBuilder) {
        if (!objectClass.getSuperclass().isAssignableFrom(Object.class)) {
            string(objectClass.getSuperclass(), object, stringBuilder);
        }

        for (Field field : objectClass.getDeclaredFields()) {
            try {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);

                    Object o = field.get(object);

                    if (o != null) {
                        stringBuilder.append(", \"").append(field.getName()).append("\": ");

                        if (o instanceof String) {
                            stringBuilder.append("\"");
                        }

                        boolean hasObject = false;

                        Class<?> c = o.getClass();

                        while (!c.getSuperclass().isAssignableFrom(Object.class) && !hasObject) {
                            for (Field f : c.getDeclaredFields()) {
                                if (!f.getType().isPrimitive() && f.trySetAccessible()) {
                                    f.setAccessible(true);

                                    Object e = f.get(o);

                                    if (e != null && ((e instanceof List && ((List<?>) e).contains(object)) || e.equals(object))) {
                                        hasObject = true;

                                        break;
                                    }
                                }
                            }

                            c = c.getSuperclass();
                        }

                        if (hasObject) {
                            if (o instanceof Entity) {
                                stringBuilder.append("{\"id:\" ").append(((Entity) o).getId()).append("}");
                            } else {
                                stringBuilder.append("{\"hashCode:\" ").append(o.hashCode()).append("}");
                            }
                        } else {
                            stringBuilder.append(o);
                        }

                        if (o instanceof String) {
                            stringBuilder.append("\"");
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                stringBuilder.append("\"").append(e.getMessage()). append("\"");
            }
        }
    }

    public static String sha256(String s) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * hash.length);

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
