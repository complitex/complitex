package ru.complitex.eirc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Pavel Sknar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestFinancialAttribute.class, TestPaymentAttribute.class, TestSPAAttribute.class, TestAccount.class })
public class AllRegistryTests {
}
