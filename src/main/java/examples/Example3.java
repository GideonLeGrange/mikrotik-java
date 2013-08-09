package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 3: Queries. Print all interfaces of a certain type. 
 *
 * @author gideon
 */
public class Example3 extends Example {

    public static void main(String... args) throws Exception {
        Example3 ex = new Example3();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException {
        List<Map<String, String>> results =  con.execute("/interface/print where type=ether");
        for (Map<String, String> result : results) {
            System.out.println(result);
        }
    }
}
