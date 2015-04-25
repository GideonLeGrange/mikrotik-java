package examples;

import me.legrange.mikrotik.ApiConnection;

/**
 *
 * @author gideon
 * @author clairtonluz
 */
 abstract class Example {
     
    protected ApiConnection connect() throws Exception {
        con = ApiConnection.connect(Config.HOST, ApiConnection.DEFAULT_PORT, 2000);
        con.login(Config.USERNAME, Config.PASSWORD);
        return con;
    }

    protected void disconnect() throws Exception {
        con.disconnect();
    }
    
    protected ApiConnection con;
    
}
