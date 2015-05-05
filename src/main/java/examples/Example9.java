package examples;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 9: Try with resources
 *
 * @author gideon
 */
public class Example9  {

    public static void main(String... args) throws Exception {
        Example9 ex = new Example9();
        ex.test();
    }

    private void test() throws MikrotikApiException, InterruptedException {
        try (ApiConnection con = ApiConnection.connect(Config.HOST, ApiConnection.DEFAULT_PORT, 2000)) {
            con.login(Config.USERNAME, Config.PASSWORD);
            con.execute("/user/add name=eric");
        }
    }
}
