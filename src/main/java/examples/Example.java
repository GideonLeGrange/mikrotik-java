package examples;

import me.legrange.mikrotik.ApiConnection;

/**
 *
 * @author gideon
 */
 abstract class Example {
     
    protected void connect() throws Exception {
        con = ApiConnection.connectTLS(Config.HOST);
        con.login(Config.USERNAME, Config.PASSWORD);
    }

    protected void disconnect() throws Exception {
        con.disconnect();
    }
    
    protected ApiConnection con;
    
}
