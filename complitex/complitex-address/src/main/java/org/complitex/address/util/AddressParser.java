package org.complitex.address.util;

/**
 * @author inheaven on 016 16.04.15 18:47
 */
public class AddressParser {
    public static String[] parseStreet(String street){
        String[] streetArray = new String[2];

        int pos = street.indexOf('.');

        if (pos == -1){
            pos = street.indexOf(' ');
        }

        if (pos > 0 && pos < street.length() - 1){
            streetArray[0] = street.substring(0, pos).trim();
            streetArray[1] = street.substring(pos + 1).trim();
        }

        return streetArray;
    }
}