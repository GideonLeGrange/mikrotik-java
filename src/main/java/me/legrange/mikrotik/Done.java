package me.legrange.mikrotik;

/**
 * Internal representation of !done
 * @author GideonLeGrange
 */
class Done extends Responsex {

    Done(String tag) {
        super(tag);
    }

    void setHash(String hash) {
        this.hash = hash;
    }
    
    String getHash() {
        return hash;
    }
    
    private String hash;
    
}
