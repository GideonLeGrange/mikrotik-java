package me.legrange.mikrotik;

/**
 * Exception thrown if the Api experiences a connection problem
 * @author GideonLeGrange
 */
public class ApiConnectionException extends MikrotikApiException {

    /** 
     * Create a new exception. 
     * 
     * @param msg The message
     */
    public ApiConnectionException(String msg) {
        super(msg);
    }

    /** 
     * Create a new exception 
     * @param msg The message
     * @param err The underlying cause 
     */
    public ApiConnectionException(String msg, Throwable err) {
        super(msg, err);
    }
    
    
    
}
