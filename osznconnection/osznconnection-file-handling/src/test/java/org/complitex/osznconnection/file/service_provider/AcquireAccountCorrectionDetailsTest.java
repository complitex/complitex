package org.complitex.osznconnection.file.service_provider;

import org.complitex.osznconnection.file.entity.subsidy.Payment;
import org.complitex.osznconnection.file.entity.subsidy.PaymentDBF;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.service_provider.exception.UnknownAccountNumberTypeException;

import java.util.Date;

/**
 *
 * @author Artem
 */
public class AcquireAccountCorrectionDetailsTest extends AbstractTest {

    public static void main(String[] args) {
        try {
            new AcquireAccountCorrectionDetailsTest().executeTest();
        } catch (DBException e) {
            System.out.println("DB error.");
            throw new RuntimeException(e);
        } catch (UnknownAccountNumberTypeException e) {
            System.out.println("Unknown account number type exception.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Payment newPayment() {
        Payment p = new Payment() {

            @Override
            public Object getField(PaymentDBF paymentDBF) {
                if (paymentDBF == PaymentDBF.DAT1) {
                    return new Date();
                } else if (paymentDBF == PaymentDBF.OWN_NUM_SR) {
                    return "1234567";
                } else {
                    throw new IllegalStateException();
                }
            }
        };
        p.setId(1L);
        p.setOutgoingDistrict("ЦЕНТРАЛЬНЫЙ");
        p.setOutgoingStreet("ФРАНТИШЕКА КРАЛА");
        p.setOutgoingStreetType("УЛ");
        p.setOutgoingBuildingNumber("25А");
        p.setOutgoingBuildingCorp("");
        p.setOutgoingApartment("19");
        return p;
    }

    @Override
    protected void test(ServiceProviderAdapter adapter) throws Exception {

    }
}
