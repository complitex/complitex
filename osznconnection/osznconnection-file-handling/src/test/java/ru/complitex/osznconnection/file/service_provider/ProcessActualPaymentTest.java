package ru.complitex.osznconnection.file.service_provider;

import ru.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;

/**
 *
 * @author Artem
 */
public class ProcessActualPaymentTest extends AbstractTest {

    public static void main(String[] args) throws Exception {
        new ProcessActualPaymentTest().executeTest();
    }

    @Override
    protected void test(ServiceProviderAdapter adapter) throws Exception {
        ActualPayment p = new ActualPayment() {

            @Override
            public void putField(String fieldName, Object object) {
                super.getDbfFields().put(fieldName, object != null ? object.toString() : null);
            }
        };
        p.setAccountNumber("1000000000");
//        try {
//            adapter.processActualPayment(new BillingContext(0L, 3L, 2L, "test", ImmutableSet.of(1L)), p, new Date());
//        } catch (DBException e) {
//            System.out.println("DB error.");
//            throw new RuntimeException(e);
//        }
        System.out.println("Status : " + p.getStatus()
                + ", P1 : " + p.getStringField(ActualPaymentDBF.P1) + ", N1 : " + p.getStringField(ActualPaymentDBF.N1)
                + ", P2 : " + p.getStringField(ActualPaymentDBF.P2) + ", N2 : " + p.getStringField(ActualPaymentDBF.N2));
    }
}
