import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.02.11 17:41
 */
public class RuntimeHang {
    public static void main(String[] args) {
        System.out.println(Arrays.toString("123456".split("\\.")));
    }
}
