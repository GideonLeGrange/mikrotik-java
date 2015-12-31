package examples;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Example 9: Try with resources
 *
 * @author gideon
 */
public class TryWithResources  {

    public static void main(String... args) throws Exception {
        TryWithResources ex = new TryWithResources();
        ex.test();
    }

    private void test() throws MikrotikApiException, InterruptedException {
        try (ApiConnection con = ApiConnection.connect(Config.HOST, ApiConnection.DEFAULT_PORT, 2000)) {
            con.login(Config.USERNAME, Config.PASSWORD);
            con.execute("/user/add name=eric");
        }
    }
}
