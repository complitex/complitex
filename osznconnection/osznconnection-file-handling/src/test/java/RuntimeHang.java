/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.02.11 17:41
 */
public class RuntimeHang {
    public static void main(String[] args) {
        System.out.println("Test:");
        double d = Double.parseDouble("2.2250738585072012e-308");
        System.out.println("Value: " + d);
    }
}
