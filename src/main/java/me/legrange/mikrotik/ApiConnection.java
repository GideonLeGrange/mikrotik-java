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

    /** default TCP port used by Mikrotik API */
    public static final int DEFAULT_PORT = 8728;

    /**
     * Create a new API connection to the give device on the supplied port
     * @param host The host to which to connect.
     * @param port The TCP port to use.
     * @return The ApiConnection 
     */
    public static ApiConnection connect(String host, int port) throws MikrotikApiException {
        return ApiConnectionImpl.connect(host, port);
    }

    /**
     * Create a new API connection to the give device on the default API port.. 
     * @param host The host to which to connect.
     * @return The ApiConnection 
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
     */
    public abstract void disconnect() throws MikrotikApiException; 

    /**
     * Log in to the remote router. 
     *
     * @param username - username of the user on the router
     * @param password - password for the user
     */
    public abstract void login(String username, String password) throws MikrotikApiException,  InterruptedException;

    /** execute a command and return a list of results. 
     * @param cmd Command to execute
     * @return The list of results
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

    /** cancel a command */
    public abstract void cancel(String tag) throws MikrotikApiException;

}