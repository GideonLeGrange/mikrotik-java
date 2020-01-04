package examples;

import me.legrange.mikrotik.MikrotikApiException;

import java.util.List;
import java.util.Map;

/**
 * Example 2: A command that returns results. Print all interfaces
 *
 * @author gideon
 */
public class NestedExpressions extends Example {

    public static void main(String... args) throws Exception {
        NestedExpressions ex = new NestedExpressions();
        ex.connect();
        ex.test("/ip/firewall/nat/print where (src-address=\"192.168.15.52\" or src-address=\"192.168.15.53\")");
        ex.test("/ip/firewall/nat/print where chain=api_test and (src-address=192.168.15.52) and action=log ");
        ex.test("/ip/firewall/nat/print where chain=api_test and (src-address=192.168.15.53 or src-address=192.168.15.52) and action=log ");
        ex.test("/ip/firewall/nat/print where chain=api_test and (src-address=\"192.168.15.53\" or src-address=\"192.168.15.52\") and action=log ");
        ex.disconnect();
    }

    private void test(String cmd) throws MikrotikApiException {
        System.out.println("Command: " + cmd);
        List<Map<String, String>> results = con.execute(cmd);
        for (Map<String, String> result : results) {
            System.out.println(result);
        }
        System.out.println();
    }
}
