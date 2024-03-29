package ru.complitex.osznconnection.file.service.file_description;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.RequestFileType;
import ru.complitex.osznconnection.file.entity.privilege.*;
import ru.complitex.osznconnection.file.entity.subsidy.*;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescriptionValidateException.ValidationError;
import ru.complitex.osznconnection.file.service.file_description.jaxb.Field;
import ru.complitex.osznconnection.file.service.file_description.jaxb.Fields;
import ru.complitex.osznconnection.file.service.file_description.jaxb.FileDescription;
import ru.complitex.osznconnection.file.service.file_description.jaxb.FileDescriptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.complitex.common.util.ResourceUtil.getFormatString;
import static ru.complitex.common.util.ResourceUtil.getString;

@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class RequestFileDescriptionBean extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(RequestFileDescriptionBean.class);
    private static final String NS = RequestFileDescriptionBean.class.getName();
    private static final String RESOURCE_BUNDLE = RequestFileDescriptionBean.class.getName();
    private final static Map<RequestFileType, RequestFileDescription> cache = new ConcurrentHashMap<>(new EnumMap<>(RequestFileType.class));

    private final static Map<RequestFileType, Class<? extends Enum<?>>> REQUEST_FILE_TYPE_MAP =
            ImmutableMap.<RequestFileType, Class<? extends Enum<?>>>builder()
                    .put(RequestFileType.ACTUAL_PAYMENT, ActualPaymentDBF.class)
                    .put(RequestFileType.PAYMENT, PaymentDBF.class)
                    .put(RequestFileType.BENEFIT, BenefitDBF.class)
                    .put(RequestFileType.SUBSIDY, SubsidyDBF.class)
                    .put(RequestFileType.SUBSIDY_TARIF, SubsidyTarifDBF.class)
                    .put(RequestFileType.DWELLING_CHARACTERISTICS, DwellingCharacteristicsDBF.class)
                    .put(RequestFileType.FACILITY_STREET_TYPE_REFERENCE, FacilityStreetTypeDBF.class)
                    .put(RequestFileType.FACILITY_STREET_REFERENCE, FacilityStreetDBF.class)
                    .put(RequestFileType.FACILITY_SERVICE_TYPE, FacilityServiceTypeDBF.class)
                    .put(RequestFileType.FACILITY_TARIF_REFERENCE, FacilityTarifDBF.class)
                    .put(RequestFileType.FACILITY_FORM2, FacilityForm2DBF.class)
                    .put(RequestFileType.FACILITY_LOCAL, FacilityLocalDBF.class)
                    .put(RequestFileType.PRIVILEGE_PROLONGATION, PrivilegeProlongationDBF.class)
                    .put(RequestFileType.DEBT, DebtDBF.class)
                    .build();

    private final static String DATE_PATTERN = "dd.MM.yyyy";


    private void insert(RequestFileDescription fileDescription) {
        sqlSession().insert(NS + ".insertFileDescription", fileDescription);
        if (!fileDescription.getFields().isEmpty()) {
            for (RequestFileFieldDescription field : fileDescription.getFields()) {
                field.setRequestFileDescriptionId(fileDescription.getId());
                sqlSession().insert(NS + ".insertFileFieldDescription", field);
            }
        }
    }


    private void delete() {
        sqlSession().delete(NS + ".deleteFileFieldDescription");
        sqlSession().delete(NS + ".deleteFileDescription");
    }


    public void update(List<RequestFileDescription> requestFileDescriptions) {
        cache.clear();
        delete();

        for (RequestFileDescription fileDescription : requestFileDescriptions) {
            insert(fileDescription);
            //prefill cache
            getFileDescription(fileDescription.getRequestFileType());
        }
    }

    public RequestFileDescription getFileDescription(RequestFileType requestFileType) {
        return cache.computeIfAbsent(requestFileType, k -> sqlSession().selectOne(NS + ".find", k.name()));
    }

    public List<RequestFileDescription> getDescription(InputStream inputStream, Locale locale)
            throws RequestFileDescriptionValidateException {
        final List<ValidationError> errors = Lists.newArrayList();

        // parsing xml step.
        FileDescriptions fileDescriptions = null;
        try {
            Schema schema;
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                final String schemaPath = RequestFileDescriptionBean.class.getPackage().getName().replace('.', '/')
                        + "/request_file_description.xsd";
                final URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource(schemaPath);
                schema = schemaFactory.newSchema(schemaUrl);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't load request_file_description.xsd scheam file.", e);
            }

            JAXBContext context = JAXBContext.newInstance(FileDescriptions.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            ValidationEventCollector validationEventCollector = new ValidationEventCollector();
            unmarshaller.setEventHandler(validationEventCollector);

            try {
                fileDescriptions = (FileDescriptions) unmarshaller.unmarshal(inputStream);
            } catch (UnmarshalException e) {
                log.error("", e);
            } finally {
                if (validationEventCollector != null && validationEventCollector.hasEvents()) {
                    // pick up first error and ignore the rest.
                    ValidationEvent event = validationEventCollector.getEvents()[0];
                    errors.add(new ValidationError(getFormatString(RESOURCE_BUNDLE, "file_validation_error", locale,
                            event.getLocator().getLineNumber(), event.getMessage())));
                    throw new RequestFileDescriptionValidateException(errors);
                }
            }
        } catch (JAXBException e) {
            log.error("", e);
            errors.add(new ValidationError(getString(RESOURCE_BUNDLE, "unexpected_jaxb_error", locale)));
            throw new RequestFileDescriptionValidateException(errors);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("Couldn't close file inout stream.", e);
            }
        }

        // transformation and validation step.
        final List<RequestFileDescription> requestFileDescriptions = Lists.newArrayList();

        for (RequestFileType requestFileType : REQUEST_FILE_TYPE_MAP.keySet()) {
            // there are must be one and only one description for each file type.
            FileDescription fileDescription = null;
            boolean tooManyDescriptionsFound = false;

            for (FileDescription fd : fileDescriptions.getFileDescriptionList()) {
                if (requestFileType.name().equals(fd.getType())) {
                    if (fileDescription == null) {
                        fileDescription = fd;
                    } else {
                        tooManyDescriptionsFound = true;
                        break;
                    }
                }
            }

            if (fileDescription == null) {
                errors.add(new ValidationError(getFormatString(RESOURCE_BUNDLE, "file_description_not_found", locale, requestFileType)));
            } else if (tooManyDescriptionsFound) {
                errors.add(new ValidationError(getFormatString(RESOURCE_BUNDLE, "file_description_too_many", locale, requestFileType)));
            } else {
                String pattern = fileDescription.getFormatters() != null
                        ? fileDescription.getFormatters().getDatePattern().getPattern()
                        : DATE_PATTERN;

                RequestFileDescription requestFileDescription = new RequestFileDescription(requestFileType.name(), pattern);
                requestFileDescriptions.add(requestFileDescription);

                // there are must be one and only one field description in each file type.
                Fields fields = fileDescription.getFields();
                Class<? extends Enum<?>> dbfClass = REQUEST_FILE_TYPE_MAP.get(requestFileType);
                for (Enum<?> dbfField : dbfClass.getEnumConstants()) {
                    String fieldName = dbfField.name();
                    Field fieldDescription = null;

                    boolean tooManyFieldDescriptionsFound = false;
                    for (Field field : fields.getFieldList()) {
                        if (fieldName.equals(field.getName())) {
                            if (fieldDescription == null) {
                                fieldDescription = field;
                            } else {
                                tooManyFieldDescriptionsFound = true;
                                break;
                            }
                        }
                    }

                    if (fieldDescription == null) {
                        errors.add(new ValidationError(getFormatString(RESOURCE_BUNDLE, "field_description_not_found", locale,
                                requestFileType, fieldName)));
                    } else if (tooManyFieldDescriptionsFound) {
                        errors.add(new ValidationError(getFormatString(RESOURCE_BUNDLE, "field_description_too_many", locale,
                                requestFileType, fieldName)));
                    } else {
                        final String type = fieldDescription.getType().value();
                        final int length = fieldDescription.getLength();
                        final Integer scale = fieldDescription.getScale();

                        //if all field description validations pass then create field and add it to file description.
                        requestFileDescription.addField(new RequestFileFieldDescription(fieldName, type, length, scale));
                    }
                }
            }
        }

        //there are must be only expected request file types.
        for (FileDescription fd : fileDescriptions.getFileDescriptionList()) {
            String fileType = fd.getType();

            RequestFileType expectedRequestFileType = null;
            for (RequestFileType requestFileType : REQUEST_FILE_TYPE_MAP.keySet()) {
                if (requestFileType.name().equals(fileType)) {
                    expectedRequestFileType = requestFileType;
                    break;
                }
            }

            if (expectedRequestFileType == null) {
                errors.add(new ValidationError(getFormatString(RESOURCE_BUNDLE, "unexpected_file_description", locale,
                        fileType)));
            } else {
                //there are must be only expected request file's field descriptions.
                for (Field f : fd.getFields().getFieldList()) {
                    String fieldName = f.getName();

                    boolean expectedField = false;
                    Class<? extends Enum<?>> dbfClass = REQUEST_FILE_TYPE_MAP.get(expectedRequestFileType);

                    for (Enum<?> dbfField : dbfClass.getEnumConstants()) {
                        if (dbfField.name().equals(fieldName)) {
                            expectedField = true;
                            break;
                        }
                    }

                    if (!expectedField) {
                        errors.add(new ValidationError(getFormatString(RESOURCE_BUNDLE, "unexpected_field_description", locale,
                                fileType, fieldName)));
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new RequestFileDescriptionValidateException(errors);
        } else {
            return requestFileDescriptions;
        }
    }
}
