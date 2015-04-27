package examples;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

import java.util.List;
import java.util.Map;

/**
 * Example 10: The try-with-resources Statement
 *
 * @author clairtonluz
 */
public class Example10 extends Example {

    public static void main(String... args) throws Exception {
        Example10 ex = new Example10();
        try (ApiConnection c = ex.connect()) {
            ex.test();
        }
        System.out.printf("\nisConnected = %B",ex.con.isConnected());
    }

    private void test() throws MikrotikApiException, InterruptedException {
       List<Map<String, String>> res = con.execute("/interface/ethernet/print");
        for (Map<String, String> r : res) {
            System.out.println(r);
        }
    }
}
