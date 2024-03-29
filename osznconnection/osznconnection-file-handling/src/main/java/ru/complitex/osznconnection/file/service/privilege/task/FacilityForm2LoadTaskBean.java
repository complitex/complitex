package ru.complitex.osznconnection.file.service.privilege.task;

import org.apache.wicket.util.string.Strings;
import ru.complitex.address.strategy.district.DistrictStrategy;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.util.ExceptionUtil;
import ru.complitex.common.util.Locales;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.util.StringUtil;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.RequestFileType;
import ru.complitex.osznconnection.file.entity.privilege.FacilityForm2;
import ru.complitex.osznconnection.file.entity.privilege.FacilityForm2DBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.LoadException;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescription;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;
import ru.complitex.osznconnection.file.service.file_description.RequestFileFieldDescription;
import ru.complitex.osznconnection.file.service.privilege.FacilityForm2Bean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author inheaven on 016 16.11.16.
 */
@Stateless
public class FacilityForm2LoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private Logger log = LoggerFactory.getLogger(FacilityForm2LoadTaskBean.class);

    private final static Class RESOURCE = FacilityForm2LoadTaskBean.class;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private FacilityForm2Bean facilityForm2Bean;

    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        String edrpou = osznOrganizationStrategy.getEdrpou(requestFile.getServiceProviderId(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        if (Strings.isEmpty(edrpou)){
            throw new LoadException("ЕДРПОУ не найден");
        }

        String zheuCode =  osznOrganizationStrategy.getServiceProviderCode(edrpou, requestFile.getOrganizationId(),
                requestFile.getUserOrganizationId());

        if (Strings.isEmpty(zheuCode)){
            throw new LoadException("Код организации не найден по ЕДРПОУ {0}", edrpou);
        }

        String district = osznOrganizationStrategy.getDistrict(requestFile.getOrganizationId());

        if (Strings.isEmpty(district)){
            throw new LoadException("Район не найден для {0}", osznOrganizationStrategy
                    .displayDomainObject(requestFile.getOrganizationId(), Locales.getSystemLocale()));
        }

        RequestFileDescription description = requestFileDescriptionBean.getFileDescription(RequestFileType.FACILITY_FORM2);

        if (description == null){
            throw new LoadException("FACILITY_FORM2 file description not found");
        }

        Cursor<FacilityForm2> cursor = serviceProviderAdapter.getFacilityForm2(requestFile.getUserOrganizationId(),
                district, zheuCode, requestFile.getBeginDate());

        if (cursor.getResultCode() > 0){
            //group by depart
            Map<String, List<FacilityForm2>> map = cursor.getData().stream()
                    .collect(Collectors.groupingBy(f -> StringUtil.emptyOnNull(f.getField("DEPART")), Collectors.toList()));

            for (String depart : map.keySet()){
                RequestFile r = new RequestFile();

                r.setUserOrganizationId(requestFile.getUserOrganizationId());
                r.setOrganizationId(requestFile.getOrganizationId());
                r.setServiceProviderId(requestFile.getServiceProviderId());
                r.setBeginDate(requestFile.getBeginDate());
                r.setType(RequestFileType.FACILITY_FORM2);
                r.setName(edrpou + depart + ".DBF");
                r.setDirectory("");

                try {
                    r.setStatus(RequestFileStatus.LOADING);
                    requestFileBean.save(r);

                    List<FacilityForm2> list = map.get(depart);


                    list.forEach(f -> {
                        f.setRequestFileId(r.getId());

                        for (FacilityForm2DBF k : FacilityForm2DBF.values()){
                            RequestFileFieldDescription field = description.getField(k.name());

                            //trim string
                            if (String.class.equals(field.getFieldType())){
                                String s = f.getStringField(k);

                                if (s != null && s.length() > field.getLength()){
                                    f.putField(k, s.substring(0, field.getLength()));

                                    log.info("facility form-2 trim field {}", s);
                                }
                            }

                            if (f.getField(k) == null){
                                f.putField(k, null);
                            }
                        }
                    });

                    facilityForm2Bean.save(list);

                    r.setStatus(RequestFileStatus.LOADED);
                    requestFileBean.save(r);
                } catch (Exception e) {
                    r.setStatus(RequestFileStatus.LOAD_ERROR);
                    requestFileBean.delete(r);

                    throw new LoadException(ExceptionUtil.getCauseMessage(e));
                }
            }
        }else {
            //noinspection Duplicates
            switch (cursor.getResultCode()){
                case -5: //Не найден р-он
                    throw new LoadException(ResourceUtil.getString(RESOURCE, "error_district_not_found"), district);
                case -18: //Не указан месяц файла
                    throw new LoadException(ResourceUtil.getString(RESOURCE, "error_null_month"));
                case -20: //Не определена организация
                    throw new LoadException(ResourceUtil.getString(RESOURCE, "error_organization_undefined"), zheuCode);
            }
        }

        return true;
    }
}
