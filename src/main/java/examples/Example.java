package examples;

import me.legrange.mikrotik.ApiConnection;

/**
 *
 * @author gideon
 */
 abstract class Example {
     
    protected void connect() throws Exception {
        con = ApiConnection.connect(Config.HOST, ApiConnection.DEFAULT_PORT, 2000);
        con.login(Config.USERNAME, Config.PASSWORD);
    }

    protected void disconnect() throws Exception {
        con.close();
    }
    
    protected ApiConnection con;
    
}
