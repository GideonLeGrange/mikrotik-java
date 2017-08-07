package me.legrange.mikrotik;

/**
 * Thrown by the Mikrotik API to indicate errors
 *
 * @author GideonLeGrange
 */
public class MikrotikApiException extends Exception {


    /** 
     * Create a new exception 
     * @param msg The message
     */
    public MikrotikApiException(String msg) {
        super(msg);
    }

 
    /** 
     * Create a new exception 
     * @param msg The message
     * @param err The underlying cause 
     */
    public MikrotikApiException(String msg, Throwable err) {
        super(msg, err);
    }
}
