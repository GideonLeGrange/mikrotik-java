package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 2: A command that returns results. Print all interfaces
 *
 * @author gideon
 */
public class SimpleCommandWithResults extends Example {

    public static void main(String... args) throws Exception {
        SimpleCommandWithResults ex = new SimpleCommandWithResults();
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
