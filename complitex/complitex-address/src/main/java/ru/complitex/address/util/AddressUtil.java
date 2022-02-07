package ru.complitex.address.util;

/**
 * inheaven on 29.02.2016.
 */
public class AddressUtil {
    public static String replaceBuildingNumberSymbol(String buildingNumber){
        if (buildingNumber != null){
            return buildingNumber
                    .replaceFirst("^0+(?!$)", "")
                    .replaceAll("F|f", "А")
                    .replaceAll("L|l", "Д")
                    .replaceAll(",", "Б");
        }

        return null;
    }

    public static String replaceBuildingCorpSymbol(String buildingCorp){
        if (buildingCorp != null){
            return buildingCorp.replaceFirst("^0+(?!$)", "");
        }

        return null;
    }

    public static String replaceApartmentSymbol(String apartment){
        if (apartment != null){
            return apartment.replaceFirst("^0+(?!$)", "");
        }

        return null;
    }

}
