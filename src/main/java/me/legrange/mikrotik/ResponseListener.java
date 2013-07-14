package me.legrange.mikrotik;

/**
 * A listener that receives life cycle command events from the Mikrotik API,
 * and not just results. 
 * @author GideonLeGrange
 */
public interface ResponseListener extends ResultListener {
    
    /** called if the command associated with this listener experiences an error */
    void error(MikrotikApiException ex);
    
    /** called when the command associated with this listener is done */
    void completed();
   
    
}
