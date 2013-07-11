package me.legrange.mikrotik;

/**
 * Implement this interface to receive command results from the Mikrotik Api.
 * @author GideonLeGrange
 */
public interface ResultListener {
    
    void receive(Result result);
    
}
