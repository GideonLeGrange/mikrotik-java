package me.legrange.mikrotik.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A result from an API command. 
 * @author GideonLeGrange
 */
class Result extends Response implements Map<String, String> {

    public String get(String key) {
        return map.get(key);
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("tag=%s, data=%s", getTag(), map);
    }

    public int size() {
        return map.size();
    }

    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    public String get(Object o) {
        return map.get(o);
    }

    public String put(String k, String v) {
        return map.put(k, v);
    }

    public String remove(Object o) {
        return map.remove(o);
    }

    public void putAll(Map<? extends String, ? extends String> map) {
        this.map.putAll(map);
    }

    public void clear() {
        map.clear();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<String> values() {
        return map.values();
    }

    public Set<Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    Result() {
        super(null);
        this.map = new HashMap<String, String>();
    }
   
    private final Map<String, String> map;

}
