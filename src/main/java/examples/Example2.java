package examples;

import java.util.List;
import me.legrange.mikrotik.MikrotikApiException;
import me.legrange.mikrotik.Result;

/**
 * Example 2: Print all interfaces
 *
 * @author gideon
 */
public class Example2 extends Example {

    public static void main(String... args) throws Exception {
        Example2 ex = new Example2();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException {
        List<Result> results =  con.execute("/interface/print");
        for (Result result : results) {
            System.out.println(result);
        }
    }
}
