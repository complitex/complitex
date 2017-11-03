package ru.complitex.pspoffice.backend.adapter;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 03.11.2017 15:31
 */
public class PersonAdapter {
    public static Map<String, String> adaptNames(Map<Locale, String> map){
        return map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getLanguage(), Map.Entry::getValue));
    }
}
