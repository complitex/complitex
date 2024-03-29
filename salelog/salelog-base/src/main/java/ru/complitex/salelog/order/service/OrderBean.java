package ru.complitex.salelog.order.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.strategy.SequenceBean;
import ru.complitex.common.util.DateUtil;
import ru.complitex.salelog.order.entity.Order;
import ru.complitex.salelog.order.entity.ProductSale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 */
@Stateless
public class OrderBean extends AbstractBean {
    private static final String NS = OrderBean.class.getName();
    public static final String ENTITY = "order";

    @EJB
    private SequenceBean sequenceBean;


    public void archive(Order object) {
        if (object.getEndDate() == null) {
            object.setEndDate(DateUtil.getCurrentDate());
        }
        sqlSession().update(NS + ".updateOrderEndDate", object);
    }

    public List<Order> getOrder(long id) {
        List<Order> orders = sqlSession().selectList(NS + ".selectOrderById", id);
        removeExcess(orders);
        return orders;
    }

    private void removeExcess(List<Order> orders) {
        List<ProductSale> cache = Lists.newArrayList();
        for (Order order : orders) {
            for (ProductSale sale : order.getProductSales()) {
                if (order.getEndDate() == null && sale.getEndDate() != null) {
                    cache.add(sale);
                } else if (order.getBeginDate().before(sale.getBeginDate())) {
                    cache.add(sale);
                } else if (order.getEndDate() != null && sale.getBeginDate().after(order.getEndDate())) {
                    cache.add(sale);
                } else if (order.getBeginDate() != null && sale.getEndDate() != null &&
                        order.getBeginDate().compareTo(sale.getEndDate()) >= 0) {
                   cache.add(sale);
                }
            }
            order.getProductSales().removeAll(cache);
            cache.clear();
        }
    }

    public Order getOrderByPkId(long id) {
        Order order = sqlSession().selectOne(NS + ".selectOrderByPkId", id);
        removeExcess(ImmutableList.of(order));
        return order;
    }

    public List<Order> getOrders(FilterWrapper<OrderExt> filter) {
        List<Order> orders = sqlSession().selectList(NS + ".selectOrders", filter);
        removeExcess(orders);
        return orders;
    }

    public Long getCount(FilterWrapper<OrderExt> filter) {
        return sqlSession().selectOne(NS + ".countOrders", filter);
    }


    public void save(Order order) {
        if (order.getId() == null) {
            create(order);
        } else {
            update(order);
        }
    }

    private void create(Order order) {
        order.setId(sequenceBean.nextId(ENTITY));
        order.setCreateDate(DateUtil.getCurrentDate());
        sqlSession().insert(NS + ".insertOrder", order);

        for (ProductSale productSale : order.getProductSales()) {
            productSale.setOrderId(order.getId());
            sqlSession().insert(NS + ".insertProductSale", productSale);
        }
    }

    private void update(Order order) {
        Order oldObject = getOrderByPkId(order.getPkId());
        if (EqualsBuilder.reflectionEquals(oldObject, order)) {
            return;
        }
        archive(oldObject);
        order.setBeginDate(oldObject.getEndDate());
        sqlSession().insert(NS + ".insertOrder", order);

        // change and remove product sales
        for (ProductSale oldSale : oldObject.getProductSales()) {
            boolean removed = true;
            for (ProductSale sale : order.getProductSales()) {
                if (sale.getPkId() != null &&
                        sale.getProduct().equals(oldSale.getProduct())) {
                    if (!EqualsBuilder.reflectionEquals(sale, oldSale)) {
                        archive(oldSale);
                        sale.setBeginDate(oldObject.getEndDate());
                        sqlSession().insert(NS + ".insertProductSale", sale);
                    }
                    removed = false;
                    break;
                }
            }
            if (removed) {
                archive(oldSale);
            }
        }

        // create new product sales
        for (ProductSale sale : order.getProductSales()) {
            if (sale.getPkId() == null) {
                sale.setOrderId(order.getId());
                sqlSession().insert(NS + ".insertProductSale", sale);
            }
        }
    }

    private void archive(ProductSale object) {
        if (object.getEndDate() == null) {
            object.setEndDate(DateUtil.getCurrentDate());
        }
        sqlSession().update(NS + ".updateProductSaleEndDate", object);
    }

    public static class OrderExt extends Order {
        private Date createDateFrom;
        private Date createDateTo;
        private String productCode;

        public Date getCreateDateFrom() {
            return createDateFrom;
        }

        public void setCreateDateFrom(Date createDateFrom) {
            this.createDateFrom = createDateFrom;
        }

        public Date getCreateDateTo() {
            return createDateTo;
        }

        public void setCreateDateTo(Date createDateTo) {
            this.createDateTo = createDateTo;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }
    }
}

