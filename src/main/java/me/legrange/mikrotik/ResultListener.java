package me.legrange.mikrotik;

import java.util.Map;

/**
 * Implement this interface to receive command results from the Mikrotik Api.
 * @author GideonLeGrange
 */
public interface ResultListener {
    
    void receive(Map<String, String> result);
    
}
