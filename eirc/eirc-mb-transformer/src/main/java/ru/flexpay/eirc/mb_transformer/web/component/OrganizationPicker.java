package ru.flexpay.eirc.mb_transformer.web.component;

import org.apache.ibatis.session.SqlSessionManager;
import org.apache.wicket.model.IModel;
import org.complitex.common.mybatis.SqlSessionFactoryBean;
import org.complitex.common.service.ConfigBean;
import ru.flexpay.eirc.mb_transformer.entity.MbTransformerConfig;

import javax.ejb.EJB;

/**
 * @author Pavel Sknar
 */
public class OrganizationPicker extends org.complitex.common.web.component.organization.OrganizationPicker {

    @EJB
    private ConfigBean configBean;

    public OrganizationPicker(String id, IModel<Long> model, Long organizationTypeId) {
        super(id, model, organizationTypeId);

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean() {
            @Override
            public SqlSessionManager getSqlSessionManager() {
                return getSqlSessionManager(configBean.getString(MbTransformerConfig.EIRC_DATA_SOURCE), "remote");
            }
        };


        organizationStrategy.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
