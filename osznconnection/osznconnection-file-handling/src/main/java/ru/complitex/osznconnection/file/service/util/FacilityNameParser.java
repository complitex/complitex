package ru.complitex.osznconnection.file.service.util;

import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.PersonalName;

public final class FacilityNameParser {

    private FacilityNameParser() {
    }

    public static PersonalName parse(String fio) {
        String firstName = "";
        String middleName = "";
        String lastName = "";
        if (!Strings.isEmpty(fio)) {
            String[] parts = fio.split(" ", 3);
            if (parts.length > 0) {
                lastName = parts[0];
                if (parts.length > 1) {
                    firstName = parts[1];
                    if (parts.length > 2) {
                        middleName = parts[2];
                    }
                }
            }
        }
        return new PersonalName(firstName, middleName, lastName);
    }
}
