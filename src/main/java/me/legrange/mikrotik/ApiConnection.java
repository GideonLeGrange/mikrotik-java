package me.legrange.mikrotik;

import java.util.List;
import java.util.Map;
import me.legrange.mikrotik.impl.ApiConnectionImpl;

/**
 * The Mikrotik API connection. This is the class used to connect to a remote
 * Mikrotik and send commands to it.
 *
 * @author GideonLeGrange
 */
public abstract class ApiConnection {

    /**
     * Create a new API connection to the give device on the supplied port, using anonymous TLS for encryption.
     * @param host The host to which to connect.
     * @param port The TCP port to use.
     * @return The ApiConnection 
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a problem connecting
     */
    public static ApiConnection connectTLS(String host, int port) throws MikrotikApiException {
        return ApiConnectionImpl.connect(host, port, true);
    }
    
    
    /**
     * Create a new API connection to the give device on the default API port, using anonymous TLS for encryption. 
     * @param host The host to which to connect.
     * @return The ApiConnection 
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a problem connecting
     */
    public static ApiConnection connectTLS(String host) throws MikrotikApiException {
        return ApiConnectionImpl.connect(host, DEFAULT_TLS_PORT, true);
    }


    /**
     * Create a new API connection to the give device on the supplied port
     * @param host The host to which to connect.
     * @param port The TCP port to use.
     * @return The ApiConnection 
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a problem connecting
     */
    public static ApiConnection connect(String host, int port) throws MikrotikApiException {
        return ApiConnectionImpl.connect(host, port, false);
    }

    /**
     * Create a new API connection to the give device on the default API port.
     * @param host The host to which to connect.
     * @return The ApiConnection 
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a problem connecting
     */
   public static ApiConnection connect(String host) throws MikrotikApiException {
        return connect(host, DEFAULT_PORT);
    }

    /**
     * Check the state of connection.
     *
     * @return if connection is established to router it returns true.
     */
    public abstract boolean isConnected();

    /**
     * Disconnect from the remote API
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a problem disconnecting
     */
    public abstract void disconnect() throws MikrotikApiException; 

    /**
     * Log in to the remote router. 
     *
     * @param username - username of the user on the router
     * @param password - password for the user
     * @throws me.legrange.mikrotik.MikrotikApiException
     * @throws java.lang.InterruptedException
     */
    public abstract void login(String username, String password) throws MikrotikApiException,  InterruptedException;

    /** execute a command and return a list of results. 
     * @param cmd Command to execute
     * @return The list of results
     * @throws me.legrange.mikrotik.MikrotikApiException
     */
    public abstract List<Map<String, String>> execute(String cmd) throws MikrotikApiException;

    /** execute a command and attach a result listener to receive it's results. 
     * 
     * @param cmd Command to execute
     * @param lis ResultListener that will receive the results
     * @return A command object that can be used to cancel the command.
     * @throws MikrotikApiException 
     */
    public abstract String execute(String cmd, ResultListener lis) throws MikrotikApiException;

    /** cancel a command
     * @param tag The tag of the command to cancel
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a problem canceling the command */
    public abstract void cancel(String tag) throws MikrotikApiException;

    /** default TCP port used by Mikrotik API */
    private static final int DEFAULT_PORT = 8728;
    /** default TCP TLS port used by Mikrotik API */
    private static final int DEFAULT_TLS_PORT = 8729;
        
}