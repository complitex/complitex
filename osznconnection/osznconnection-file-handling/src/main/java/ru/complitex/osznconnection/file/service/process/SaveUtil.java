package ru.complitex.osznconnection.file.service.process;

import com.google.common.collect.ImmutableSet;
import ru.complitex.common.entity.IExecutorObject;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.osznconnection.file.entity.AbstractRequest;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.subsidy.PaymentDBF;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import ru.complitex.osznconnection.file.service.StatusRenderUtil;
import ru.complitex.osznconnection.file.service.exception.StorageNotFoundException;
import ru.complitex.osznconnection.file.service.warning.IWarningRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.12.10 14:59
 */
public final class SaveUtil {

    private SaveUtil() {      }
    private final Logger log = LoggerFactory.getLogger(SaveUtil.class);

    private final static String FILE_ENCODING = "cp1251";
    private final static String RESULT_FILE_NAME = "Result";
    private final static String RESULT_FILE_EXT = "txt";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    private final static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private final static Locale SYSTEM = new Locale("ru");
    private final static Set<RequestStatus> NOT_REPORTABLE_STATUSES = ImmutableSet.of(RequestStatus.PROCESSED,
            RequestStatus.ACCOUNT_NUMBER_RESOLVED, RequestStatus.ADDRESS_CORRECTED);

    public static void createResult(List<RequestFileGroup> processed, IWarningRenderer warningRenderer)
            throws StorageNotFoundException {
        Map<String, List<RequestFileGroup>> catalog = new HashMap<String, List<RequestFileGroup>>();

        //sort groups by directory
        for (IExecutorObject object : processed) {
            RequestFileGroup group = (RequestFileGroup) object;

            List<RequestFileGroup> list = catalog.computeIfAbsent(group.getDirectory(), k -> new ArrayList<>());

            list.add(group);
        }

        for (String directory : catalog.keySet()) {
            writeDirectory(directory, catalog.get(directory), warningRenderer);
        }
    }

    private static void writeDirectory(String directory, List<RequestFileGroup> groups, IWarningRenderer warningRenderer)
            throws StorageNotFoundException {
        Writer writer = null;
        try {
            if (groups != null && !groups.isEmpty()) {
                final long userOrganizationId = groups.get(0).getPaymentFile().getUserOrganizationId();
                final long osznId = groups.get(0).getPaymentFile().getOrganizationId();

                final Date now = DateUtil.getCurrentDate();
                final String name = RESULT_FILE_NAME + "_" + sdfFile.format(now) + "." + RESULT_FILE_EXT;

                File file = RequestFileStorage.INSTANCE.createOutputRequestFileDirectory(
                        RequestFileStorage.INSTANCE.getRequestFilesStorageDirectory(userOrganizationId, osznId,
                        RequestFileDirectoryType.SAVE_PAYMENT_BENEFIT_FILES_DIR), name, directory);

                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), FILE_ENCODING));

                writer.write("Время выгрузки: " + sdf.format(now));
                writer.write("\n\nКаталог: " + directory);

                int requestCount = 0;

                for (RequestFileGroup group : groups) {
                    int count = group.getPaymentFile().getDbfRecordCount();

                    writer.write("\n" + group.getPaymentFile().getName() + ", " + group.getBenefitFile().getName()
                            + " - Запросов: " + count);
                    writeErrorStatus(group.getPaymentFile().getRequests(), writer, warningRenderer, false);
                    writeErrorStatus(group.getBenefitFile().getRequests(), writer, warningRenderer, false);

                    requestCount += count;
                }

                writer.write("\n\nВсего пар файлов: " + groups.size());
                writer.write("\nВсего запросов: " + requestCount);

                writer.close();
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(SaveUtil.class).error("Ошибка сохранения файла Result.txt", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LoggerFactory.getLogger(SaveUtil.class).error("Ошибка закрытия файла Result.txt", e);
                }
            }
        }
    }

    private static void writeErrorStatus(List<? extends AbstractRequest> requests, Writer fileWriter,
            IWarningRenderer warningRenderer, boolean onlyWarning) throws IOException {
        if (requests != null) {
            for (AbstractRequest request : requests) {
                if (!NOT_REPORTABLE_STATUSES.contains(request.getStatus())) {
                    boolean hasWarning = request.getWarnings() != null && !request.getWarnings().isEmpty();

                    String warning = "";
                    if (hasWarning) {
                        warning = " (" + warningRenderer.display(request.getWarnings(), SYSTEM) + ")";
                    }

                    if (hasWarning || !onlyWarning) {
                        fileWriter.write("\n\tдело №" + request.getDbfFields().get(PaymentDBF.OWN_NUM.name())
                                + " - " + getString(request.getStatus().name()) + warning);
                    }
                }
            }
        }
    }

    private static String getString(String key) {
        String s = ResourceUtil.getString(StatusRenderUtil.class.getName(), key, SYSTEM);
        return s != null ? s : key;
    }
}
