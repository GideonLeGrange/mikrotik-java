package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 2: A command that returns results. Print all interfaces
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
        List<Map<String, String>> results =  con.execute("/interface/print");
        for (Map<String, String> result : results) {
            System.out.println(result);
        }
    }
}
