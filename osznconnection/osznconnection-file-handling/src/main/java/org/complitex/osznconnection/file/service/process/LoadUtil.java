package org.complitex.osznconnection.file.service.process;

import org.apache.commons.codec.digest.DigestUtils;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import org.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import org.complitex.osznconnection.file.service.exception.StorageNotFoundException;
import org.complitex.osznconnection.file.service.process.RequestFileStorage.RequestFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.complitex.common.util.DateUtil.newDate;
import static org.complitex.osznconnection.file.entity.FileHandlingConfig.*;
import static org.complitex.osznconnection.file.service.process.RequestFileDirectoryType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:57
 */
public class LoadUtil {

    private LoadUtil() {
    }

    private static String getConfigString(FileHandlingConfig config) {
        return EjbBeanLocator.getBean(ConfigBean.class).getString(config, true);
    }

    private static String getPattern(String mask, int month, int year) {
        return mask.replace("{MM}", (month <= 9 ? "0" + month : "" + month)).replace("{YY}",
                String.valueOf(year).substring(2, 4)).replace("{YYYY}", "" + year);
    }

    private static boolean isMatches(FileHandlingConfig config, String name) {
        return Pattern.compile(getConfigString(config), CASE_INSENSITIVE).matcher(name).matches();
    }

    private static boolean isMatches(FileHandlingConfig config, String name, int month, int year) {
        return Pattern.compile(getPattern(getConfigString(config), month, year), CASE_INSENSITIVE).matcher(name).matches();
    }

    private static RequestFiles getInputSubsidyTarifFiles(long userOrganizationId, long osznId, 
            final FileHandlingConfig mask) throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId, REFERENCES_DIR,
                file -> isMatches(mask, file.getName()));
    }

    private static RequestFiles getInputFacilityReferenceFiles(long userOrganizationId, long osznId,
            final FileHandlingConfig mask) throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId, REFERENCES_DIR,
                file -> isMatches(mask, file.getName()));
    }

    private static RequestFiles getInputPaymentBenefitFiles(FileHandlingConfig mask, int month, int year, long osznId, long userOrganizationId)
            throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId, LOAD_PAYMENT_BENEFIT_FILES_DIR,
                file -> isMatches(mask, file.getName(), month, year));
    }

    private static RequestFiles getInputActualPaymentFiles(long userOrganizationId, long osznId, final FileHandlingConfig mask,
            final int month, final int year) throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId, LOAD_ACTUAL_PAYMENT_DIR,
                file -> isMatches(mask, file.getName(), month, year));
    }

    private static RequestFiles getInputSubsidyFiles(long userOrganizationId, long osznId, final FileHandlingConfig mask,
            final int month, final int year) throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId, LOAD_SUBSIDY_DIR,
                file -> isMatches(mask, file.getName(), month, year));
    }

    private static RequestFiles getInputDwellingCharacteristicsFiles(long userOrganizationId, long osznId,
            final FileHandlingConfig mask) throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId, LOAD_DWELLING_CHARACTERISTICS_DIR,
                file -> isMatches(mask, file.getName()));
    }

    private static RequestFiles getInputFacilityServiceTypeFiles(long userOrganizationId, long osznId,
            final FileHandlingConfig mask) throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId, LOAD_FACILITY_SERVICE_TYPE_DIR,
                file -> isMatches(mask, file.getName()));
    }

    private static String getSuffix(String name) {
        return name.replaceAll("\\D*", "");
    }

    private static String getPrivilegeSuffix(String name) {
        int p = name.indexOf(".");

        return p > 0 ? name.substring(p+1) : "";
    }

    private static String getPrefix(String name){
        int p = name.indexOf(".");

        return p > 0 ? name.substring(0, p) : "";
    }

    private static RequestFile newPaymentBenefitRequestFile(File file, RequestFileType type, String relativeDirectory,
            long osznId, Date beginDate, Date endDate) {
        RequestFile requestFile = new RequestFile();

        requestFile.setName(file.getName());
        requestFile.setType(type);
        requestFile.setDirectory(relativeDirectory);
        requestFile.setLength(file.length());
        requestFile.setAbsolutePath(file.getAbsolutePath());
        requestFile.setOrganizationId(osznId);
        requestFile.setBeginDate(beginDate);
        requestFile.setEndDate(endDate);
        return requestFile;
    }

       public static LoadGroupParameter<RequestFileGroup> getLoadGroupParameter(long userOrganizationId, long osznId,
                                                           int monthFrom, int monthTo, int year) throws StorageNotFoundException {

        Map<String, Map<String, RequestFileGroup>> requestFileGroupsMap = new HashMap<>();

        //payment
        for (int month = monthFrom; month <= monthTo; ++month) {
            RequestFiles requestFiles = getInputPaymentBenefitFiles(PAYMENT_FILENAME_MASK, month, year,
                    osznId, userOrganizationId);

            List<File> payments = requestFiles.getFiles();
            for (int i = 0; i < payments.size(); ++i) {
                File file = payments.get(i);

                RequestFileGroup group = new RequestFileGroup();
                group.setGroupType(RequestFileGroupType.SUBSIDY_GROUP);

                group.setPaymentFile(newPaymentBenefitRequestFile(file, RequestFileType.PAYMENT,
                        RequestFileStorage.INSTANCE.getRelativeParent(file, requestFiles.getPath()),
                        osznId, newDate(year, month), null));

                payments.remove(i);
                i--;

                Map<String, RequestFileGroup> map = requestFileGroupsMap.get(file.getParent());

                if (map == null) {
                    map = new HashMap<>();
                    requestFileGroupsMap.put(file.getParent(), map);
                }

                map.put(getSuffix(file.getName()), group);
            }
        }

        List<RequestFile> linkError = new ArrayList<>();

        //benefit
        for (int month = monthFrom; month <= monthTo; ++month) {
            RequestFiles requestFiles = getInputPaymentBenefitFiles(BENEFIT_FILENAME_MASK, month, year,
                    osznId, userOrganizationId);

            List<File> benefits = requestFiles.getFiles();
            for (File file : benefits) {
                RequestFile requestFile = newPaymentBenefitRequestFile(file, RequestFileType.BENEFIT,
                        RequestFileStorage.INSTANCE.getRelativeParent(file, requestFiles.getPath()),
                        osznId, newDate(year, month), null);

                Map<String, RequestFileGroup> map = requestFileGroupsMap.get(file.getParent());

                if (map != null) {
                    RequestFileGroup group = map.get(getSuffix(file.getName()));

                    if (group != null) {
                        group.setBenefitFile(requestFile);
                        continue;
                    }
                }

                linkError.add(requestFile);
            }
        }

        List<RequestFileGroup> requestFileGroups = new ArrayList<>();

        for (Map<String, RequestFileGroup> map : requestFileGroupsMap.values()) {
            for (RequestFileGroup group : map.values()) {
                if (group.getBenefitFile() != null) {
                    requestFileGroups.add(group);
                } else {
                    RequestFile payment = group.getPaymentFile();

                    linkError.add(payment);
                }
            }
        }

        return new LoadGroupParameter<>(requestFileGroups, linkError);
    }

    public static LoadGroupParameter<PrivilegeFileGroup> getLoadPrivilegeGroupParameter(long userOrganizationId, long osznId,
                                                           int month, int year) throws StorageNotFoundException {
        List<RequestFile> dwellingCharacteristicsRequestFiles = getDwellingCharacteristics(userOrganizationId, osznId, month, year);
        List<RequestFile> facilityServiceTypeRequestFiles = getFacilityServiceTypes(userOrganizationId, osznId, month, year);

        Map<String, List<RequestFile>> map = Stream
                .concat(dwellingCharacteristicsRequestFiles.stream(), facilityServiceTypeRequestFiles.stream())
                .collect(Collectors.groupingBy(r -> r.getDirectory() + r.getShortName()));

        List<PrivilegeFileGroup> list = new ArrayList<>();

        map.values().forEach(v ->{
            PrivilegeFileGroup group = new PrivilegeFileGroup();
            group.setId(System.nanoTime());

            v.forEach(f -> {
                f.setGroupId(group.getId());

                if (f.getType().equals(RequestFileType.DWELLING_CHARACTERISTICS)){
                    group.setDwellingCharacteristicsRequestFile(f);
                }else if (f.getType().equals(RequestFileType.FACILITY_SERVICE_TYPE)){
                    group.setFacilityServiceTypeRequestFile(f);
                }
            });

            list.add(group);
        } );

        return new LoadGroupParameter<>(list, new ArrayList<>());
    }


    public static List<RequestFile> getSubsidyTarifs(long userOrganizationId, long osznId, int month, int year)
            throws StorageNotFoundException {
        List<RequestFile> subsidyTarifs = new ArrayList<>();

        RequestFiles requestFiles = getInputSubsidyTarifFiles(userOrganizationId, osznId, SUBSIDY_TARIF_FILENAME_MASK);

        List<File> files = requestFiles.getFiles();
        for (File file : files) {
            //fill fields
            RequestFile requestFile = newRequestFile(osznId, month, year, requestFiles, file, RequestFileType.SUBSIDY_TARIF);

            subsidyTarifs.add(requestFile);
        }
        return subsidyTarifs;
    }

    private static RequestFile newRequestFile(Long userOrganizationId, Long osznId, int month, int year,
                                              RequestFiles requestFiles, File file, RequestFileType requestFileType) {
        RequestFile requestFile = new RequestFile();

        requestFile.setName(file.getName());
        requestFile.setLength(file.length());
        requestFile.setAbsolutePath(file.getAbsolutePath());
        requestFile.setDirectory(RequestFileStorage.INSTANCE.getRelativeParent(file, requestFiles.getPath()));
        requestFile.setOrganizationId(osznId);
        requestFile.setUserOrganizationId(userOrganizationId);
        requestFile.setBeginDate(newDate(year, month));
        requestFile.setType(requestFileType);

        return requestFile;
    }

    private static RequestFile newRequestFile(long osznId, int month, int year, RequestFiles requestFiles, File file,
                                              RequestFileType subsidyTarif) {
        return newRequestFile(null, osznId, month, year, requestFiles, file, subsidyTarif);
    }

    public static List<RequestFile> getActualPayments(long userOrganizationId, long osznId, int monthFrom,
            int monthTo, int year) throws StorageNotFoundException {
        List<RequestFile> actualPayments = new ArrayList<>();

        for (int month = monthFrom; month <= monthTo; ++month) {
            RequestFiles requestFiles = getInputActualPaymentFiles(userOrganizationId, osznId,
                    ACTUAL_PAYMENT_FILENAME_MASK, month, year);

            List<File> files = requestFiles.getFiles();
            for (File file : files) {
                //fill fields
                RequestFile requestFile = newRequestFile(osznId, month, year, requestFiles, file,
                        RequestFileType.ACTUAL_PAYMENT);

                actualPayments.add(requestFile);
            }
        }
        return actualPayments;
    }

    public static List<RequestFile> getSubsidies(long userOrganizationId, long osznId, int monthFrom,
            int monthTo, int year) throws StorageNotFoundException {
        List<RequestFile> subsidies = new ArrayList<>();

        for (int month = monthFrom; month <= monthTo; ++month) {
            RequestFiles requestFiles = getInputSubsidyFiles(userOrganizationId, osznId, SUBSIDY_FILENAME_MASK, month, year);

            List<File> files = requestFiles.getFiles();
            for (File file : files) {
                //fill fields
                RequestFile requestFile = newRequestFile(osznId, month, year, requestFiles, file, RequestFileType.SUBSIDY);
                try {
                    requestFile.setCheckSum(DigestUtils.md5Hex(new FileInputStream(file)));
                } catch (IOException e) {
                    //md5
                }


                subsidies.add(requestFile);
            }
        }
        return subsidies;
    }

    public static List<RequestFile> getDwellingCharacteristics(long userOrganizationId, long osznId, int month, int year)
            throws StorageNotFoundException {
        List<RequestFile> dwellingCharacteristicsFiles = new ArrayList<RequestFile>();

        RequestFiles requestFiles = getInputDwellingCharacteristicsFiles(userOrganizationId, osznId,
                DWELLING_CHARACTERISTICS_INPUT_FILENAME_MASK);

        List<File> files = requestFiles.getFiles();
        for (File file : files) {
            //fill fields
            RequestFile requestFile = newRequestFile(userOrganizationId, osznId, month, year, requestFiles, file,
                    RequestFileType.DWELLING_CHARACTERISTICS);

            dwellingCharacteristicsFiles.add(requestFile);
        }
        return dwellingCharacteristicsFiles;
    }



    public static List<RequestFile> getFacilityServiceTypes(long userOrganizationId, long osznId, int month, int year)
            throws StorageNotFoundException {
        List<RequestFile> facilityServiceTypeFiles = new ArrayList<>();

        RequestFiles requestFiles = getInputFacilityServiceTypeFiles(userOrganizationId, osznId,
                FACILITY_SERVICE_TYPE_INPUT_FILENAME_MASK);

        List<File> files = requestFiles.getFiles();
        for (File file : files) {
            //fill fields
            RequestFile requestFile = newRequestFile(userOrganizationId, osznId, month, year, requestFiles, file,
                    RequestFileType.FACILITY_SERVICE_TYPE);

            facilityServiceTypeFiles.add(requestFile);
        }
        return facilityServiceTypeFiles;
    }

    public static List<RequestFile> getFacilityStreetTypeReferences(long userOrganizationId, long osznId, int month, int year)
            throws StorageNotFoundException {
        List<RequestFile> streetTypeFiles = new ArrayList<>();

        RequestFiles requestFiles = getInputFacilityReferenceFiles(userOrganizationId, osznId,
                FACILITY_STREET_TYPE_REFERENCE_FILENAME_MASK);

        List<File> files = requestFiles.getFiles();
        for (File file : files) {
            //fill fields
            RequestFile requestFile = newRequestFile(osznId, month, year, requestFiles, file,
                    RequestFileType.FACILITY_STREET_TYPE_REFERENCE);
            streetTypeFiles.add(requestFile);
        }
        return streetTypeFiles;
    }

    public static List<RequestFile> getFacilityStreetReferences(long userOrganizationId, long osznId, int month, int year)
            throws StorageNotFoundException {
        List<RequestFile> streetFiles = new ArrayList<>();

        RequestFiles requestFiles = getInputFacilityReferenceFiles(userOrganizationId, osznId,
                FACILITY_STREET_REFERENCE_FILENAME_MASK);

        List<File> files = requestFiles.getFiles();
        for (File file : files) {
            //fill fields
            RequestFile requestFile = newRequestFile(osznId, month, year, requestFiles, file,
                    RequestFileType.FACILITY_STREET_REFERENCE);
            streetFiles.add(requestFile);
        }
        return streetFiles;
    }

    public static List<RequestFile> getFacilityTarifReferences(long userOrganizationId, long osznId, int month, int year)
            throws StorageNotFoundException {
        List<RequestFile> facilityTarifFiles = new ArrayList<>();

        RequestFiles requestFiles = getInputFacilityReferenceFiles(userOrganizationId, osznId,
                FACILITY_TARIF_REFERENCE_FILENAME_MASK);

        List<File> files = requestFiles.getFiles();
        for (File file : files) {
            //fill fields
            RequestFile requestFile = newRequestFile(osznId, month, year, requestFiles, file,
                    RequestFileType.FACILITY_TARIF_REFERENCE);

            facilityTarifFiles.add(requestFile);
        }
        return facilityTarifFiles;
    }

    public static List<RequestFile> getPrivilegeProlongation(RequestFileSubType subType, Long userOrganizationId,
                                                             Long osznId, int month, int year)
            throws StorageNotFoundException {
        FileHandlingConfig fileHandlingConfig = subType.equals(RequestFileSubType.PRIVILEGE_PROLONGATION_S)
                ? PRIVILEGE_PROLONGATION_S_FILENAME_MASK
                : PRIVILEGE_PROLONGATION_P_FILENAME_MASK;

        RequestFiles requestFiles = RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId,
                LOAD_PRIVILEGE_PROLONGATION_DIR, file -> isMatches(fileHandlingConfig, file.getName()));

        List<RequestFile> list = new ArrayList<>();

        for (File file : requestFiles.getFiles()) {
            RequestFile requestFile = newRequestFile(userOrganizationId, osznId, month, year, requestFiles, file,
                    RequestFileType.PRIVILEGE_PROLONGATION);
            requestFile.setSubType(subType);

            list.add(requestFile);
        }

        return list;
    }

    public static List<RequestFile> getOschadbankRequests(Long userOrganizationId, Long osznId, int month, int year)
            throws StorageNotFoundException {
        return getOschadbankFiles(userOrganizationId, osznId, month, year, LOAD_OSCHADBANK_REQUEST_DIR,
                OSCHADBANK_REQUEST_FILENAME_MASK, RequestFileType.OSCHADBANK_REQUEST);
    }

    public static List<RequestFile> getOschadbankResponses(Long userOrganizationId, Long osznId, int month, int year)
            throws StorageNotFoundException {
        return getOschadbankFiles(userOrganizationId, osznId, month, year, LOAD_OSCHADBANK_RESPONSE_DIR,
                OSCHADBANK_RESPONSE_FILENAME_MASK, RequestFileType.OSCHADBANK_RESPONSE);
    }

    private static List<RequestFile> getOschadbankFiles(Long userOrganizationId, Long osznId, int month, int year,
                                                        RequestFileDirectoryType requestFileDirectoryType,
                                                        FileHandlingConfig fileHandlingConfig,
                                                        RequestFileType requestFileType)
            throws StorageNotFoundException {
        RequestFiles requestFiles = RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId,
                requestFileDirectoryType, file -> isMatches(fileHandlingConfig, file.getName(), month, year));

        List<RequestFile> list = new ArrayList<>();

        for (File file : requestFiles.getFiles()) {
            RequestFile requestFile = newRequestFile(userOrganizationId, osznId, month, year, requestFiles, file,
                    requestFileType);

            list.add(requestFile);
        }

        return list;
    }

    public static List<RequestFile> getDebts(Long userOrganizationId, Long osznId, int month, int year)
            throws StorageNotFoundException {
        RequestFiles requestFiles = RequestFileStorage.INSTANCE.getInputRequestFiles(userOrganizationId, osznId,
                LOAD_DEBT_DIR, file -> isMatches(DEBT_FILENAME_MASK, file.getName(), month, year));

        List<RequestFile> list = new ArrayList<>();

        for (File file : requestFiles.getFiles()) {
            RequestFile requestFile = newRequestFile(userOrganizationId, osznId, month, year, requestFiles, file,
                    RequestFileType.DEBT);

            list.add(requestFile);
        }

        return list;
    }
}
