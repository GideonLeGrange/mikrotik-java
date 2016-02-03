package me.legrange.mikrotik;

import java.util.List;
import java.util.Map;
import javax.net.SocketFactory;
import me.legrange.mikrotik.impl.ApiConnectionImpl;

/**
 * The Mikrotik API connection. This is the class used to connect to a remote
 * Mikrotik and send commands to it.
 *
 * @author GideonLeGrange
 */
public abstract class ApiConnection implements AutoCloseable {

    /**
     * default TCP port used by Mikrotik API
     */
    public static final int DEFAULT_PORT = 8728;
    /**
     * default TCP TLS port used by Mikrotik API
     */
    public static final int DEFAULT_TLS_PORT = 8729;
    /**
     * default connection timeout to use when opening the connection
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
    /**
     * default command timeout used for synchronous commands
     */
    public static final int DEFAULT_COMMAND_TIMEOUT = 60000;

    
    /**
     * Create a new API connection to the give device on the supplied port using 
     * the supplied socket factory to create the socket. 
     *
     * @param fact SocketFactory to use for TCP socket creation.
     * @param host The host to which to connect.
     * @param port The TCP port to use.
     * @param timeout The connection timeout to use when opening the connection.
     * @return The ApiConnection
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a
     * problem connecting
     * @since 3.0
     */
    public static ApiConnection connect(SocketFactory fact, String host, int port, int timeout) throws MikrotikApiException {
        return ApiConnectionImpl.connect(fact, host, port, timeout);
    }

    /**
     * Create a new API connection to the give device on the default API port.
     *
     * @param host The host to which to connect.
     * @return The ApiConnection
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a
     * problem connecting
     */
    public static ApiConnection connect(String host) throws MikrotikApiException {
        return connect(SocketFactory.getDefault(), host, DEFAULT_PORT, DEFAULT_COMMAND_TIMEOUT);
    }

    /**
     * Check the state of connection.
     *
     * @return if connection is established to router it returns true.
     */
    public abstract boolean isConnected();

    /**
     * Log in to the remote router.
     *
     * @param username - username of the user on the router
     * @param password - password for the user
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if the API encounters an error on login.
     */
    public abstract void login(String username, String password) throws MikrotikApiException;

    /**
     * execute a command and return a list of results.
     *
     * @param cmd Command to execute
     * @return The list of results
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if the API encounters an error executing a command.
     */
    public abstract List<Map<String, String>> execute(String cmd) throws MikrotikApiException;

    /**
     * execute a command and attach a result listener to receive it's results.
     *
     * @param cmd Command to execute
     * @param lis ResultListener that will receive the results
     * @return A command object that can be used to cancel the command.
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if the API encounters an error executing a command.
     */
    public abstract String execute(String cmd, ResultListener lis) throws MikrotikApiException;

    /**
     * cancel a command
     *
     * @param tag The tag of the command to cancel
     * @throws me.legrange.mikrotik.MikrotikApiException Thrown if there is a
     * problem cancelling the command
     */
    public abstract void cancel(String tag) throws MikrotikApiException;

    /**
     * set the command timeout. The command timeout is used to time out API
     * commands after a specific time.
     *
     * Note: This is not the same as the timeout value passed in the connect()
     * methods. This timeout is specific to synchronous
     * commands, that timeout is applied to opening the API socket.
     *
     * @param timeout The time out in milliseconds.
     * @throws MikrotikApiException Thrown if the timeout specified is invalid.
     * @since 2.1
     */
    public abstract void setTimeout(int timeout) throws MikrotikApiException;

    /**
     * Disconnect from the remote API
     *
     * @throws me.legrange.mikrotik.ApiConnectionException Thrown if there is a
     * problem closing the connection.
     * @since 2.2
     */
    @Override
    public abstract void close() throws ApiConnectionException;

}
