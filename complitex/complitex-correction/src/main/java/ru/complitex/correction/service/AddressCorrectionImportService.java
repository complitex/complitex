package ru.complitex.correction.service;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.entity.AddressImportFile;
import ru.complitex.address.strategy.building.BuildingStrategy;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.address.strategy.district.DistrictStrategy;
import ru.complitex.address.strategy.street.StreetStrategy;
import ru.complitex.address.strategy.street_type.StreetTypeStrategy;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.exception.ImportObjectLinkException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.correction.entity.Correction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.02.11 16:08
 */
@Stateless
public class AddressCorrectionImportService extends AbstractImportService {
    private final Logger log = LoggerFactory.getLogger(AddressCorrectionImportService.class);

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    public void process(long organizationId, long internalOrganizationId, IImportListener listener)
            throws ImportFileNotFoundException, ImportObjectLinkException, ImportFileReadException {
        importCityToCorrection(organizationId, internalOrganizationId, listener);
        importDistrictToCorrection(organizationId, internalOrganizationId, listener);
        importStreetTypeToCorrection(organizationId, internalOrganizationId, listener);
        importStreetToCorrection(organizationId, internalOrganizationId, listener);
        importBuildingToCorrection(organizationId, internalOrganizationId, listener);
    }

    /**
     * CITY_ID	REGION_ID	CITY_TYPE_ID	Название населенного пункта
     */
    public void importCityToCorrection(Long orgId, Long intOrgId, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(AddressImportFile.CITY, getRecordCount(AddressImportFile.CITY));

        CSVReader reader = getCsvReader(AddressImportFile.CITY);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                Long externalId = Long.valueOf(line[0].trim());

                //CITY_ID
                Long objectId = null;//cityStrategy.getObjectId(externalId);
                if (objectId == null) {
                    throw new ImportObjectLinkException(AddressImportFile.CITY.getFileName(), recordIndex, line[0]);
                }

                correctionBean.save(new Correction(AddressEntity.CITY.getEntityName(), externalId, objectId, line[3].trim(), orgId, null));

                listener.recordProcessed(AddressImportFile.CITY, recordIndex);
            }

            listener.completeImport(AddressImportFile.CITY, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, AddressImportFile.CITY.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }

    /**
     * DISTRICT_ID	CITY_ID	Код района	Название района
     */
    public void importDistrictToCorrection(Long orgId, Long intOrgId, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(AddressImportFile.DISTRICT, getRecordCount(AddressImportFile.DISTRICT));

        CSVReader reader = getCsvReader(AddressImportFile.DISTRICT);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                String externalId = line[0].trim();

                //DISTRICT_ID
                Long districtId = null;//districtStrategy.getObjectId(externalId);
                if (districtId == null) {
                    throw new ImportObjectLinkException(AddressImportFile.DISTRICT.getFileName(), recordIndex, line[0]);
                }

                //CITY_ID
//                if (cityStrategy.getObjectId(line[1].trim()) == null) {
//                    throw new ImportObjectLinkException(AddressImportFile.DISTRICT.getFileName(), recordIndex, line[1]);
//                }

                listener.recordProcessed(AddressImportFile.DISTRICT, recordIndex);
            }

            listener.completeImport(AddressImportFile.DISTRICT, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, AddressImportFile.DISTRICT.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }

    /**
     * STREET_TYPE_ID	Короткое наименование	Название типа улицы
     */
    public void importStreetTypeToCorrection(Long orgId, Long intOrgId, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(AddressImportFile.STREET_TYPE, getRecordCount(AddressImportFile.STREET_TYPE));

        CSVReader reader = getCsvReader(AddressImportFile.STREET_TYPE);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                Long externalId = Long.valueOf(line[0].trim());

                //STREET_TYPE_ID
                Long streetTypeId = null;// streetTypeStrategy.getObjectId(externalId);
                if (streetTypeId == null) {
                    throw new ImportObjectLinkException(AddressImportFile.STREET_TYPE.getFileName(), recordIndex, line[0]);
                }

                correctionBean.save(new Correction(AddressEntity.STREET_TYPE.getEntityName(), externalId, streetTypeId, line[2].trim(), orgId, null));

                listener.recordProcessed(AddressImportFile.STREET_TYPE, recordIndex);
            }

            listener.completeImport(AddressImportFile.STREET_TYPE, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, AddressImportFile.STREET_TYPE.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }

    /**
     * STREET_ID	CITY_ID	STREET_TYPE_ID	Название улицы
     */
    public void importStreetToCorrection(Long orgId, Long intOrgId, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(AddressImportFile.STREET, getRecordCount(AddressImportFile.STREET));

        CSVReader reader = getCsvReader(AddressImportFile.STREET);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                Long externalId = Long.valueOf(line[0].trim());

                //STREET_ID
                Long streetId = null; //streetStrategy.getObjectId(externalId);

                //CITY_ID
                Long cityObjectId = null;//cityStrategy.getObjectId(externalId);

                if (cityObjectId == null) {
                    throw new ImportObjectLinkException(AddressImportFile.STREET.getFileName(), recordIndex, line[1]);
                }

                //STREET_TYPE_ID
                Long streetTypeObjectId = null;//streetTypeStrategy.getObjectId(line[2].trim());
                if (streetTypeObjectId == null) {
                    throw new ImportObjectLinkException(AddressImportFile.STREET.getFileName(), recordIndex, line[2]);
                }

                correctionBean.save(new Correction(AddressEntity.STREET.getEntityName(), cityObjectId, streetTypeObjectId,
                        externalId, streetId, line[3].trim(), orgId, null));

                listener.recordProcessed(AddressImportFile.STREET, recordIndex);
            }

            listener.completeImport(AddressImportFile.STREET, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, AddressImportFile.STREET.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }

    /**
     * BUILDING_ID	DISTRICT_ID	STREET_ID	Номер дома	Корпус	Строение
     */
    public void importBuildingToCorrection(Long orgId, Long intOrgId, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(AddressImportFile.BUILDING, getRecordCount(AddressImportFile.BUILDING));

        CSVReader reader = getCsvReader(AddressImportFile.BUILDING);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                Long externalId = Long.valueOf(line[0].trim());

                Long buildingId = null;//buildingStrategy.getObjectId(externalId);
                if (buildingId == null) {
                    throw new ImportObjectLinkException(AddressImportFile.BUILDING.getFileName(), recordIndex, line[0]);
                }

                //STREET_ID
                Long streetObjectId = null;//streetStrategy.getObjectId(line[2].trim());
                if (streetObjectId == null) {
                    throw new ImportObjectLinkException(AddressImportFile.BUILDING.getFileName(), recordIndex, line[2]);
                }

                correctionBean.save(new Correction(AddressEntity.BUILDING.getEntityName(), streetObjectId, externalId,
                        buildingId, line[3].trim(), line[4].trim(), orgId, null));

                listener.recordProcessed(AddressImportFile.BUILDING, recordIndex);
            }

            listener.completeImport(AddressImportFile.BUILDING, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, AddressImportFile.BUILDING.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}