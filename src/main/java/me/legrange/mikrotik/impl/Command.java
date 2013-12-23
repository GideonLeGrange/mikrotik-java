package me.legrange.mikrotik.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A command sent to a Mikrotik. This internal class is used to build complex commands
 * with parameters, queries and property lists.
 *
 * @author GideonLeGrange
 */
class Command {

    @Override
    public String toString() {
        return String.format("cmd[%s] = %s, params = %s, queries = %s, props=%s ", tag, cmd, params, queries, properties);
    }

    Command(String cmd) {
        if (!cmd.startsWith("/")) {
            cmd = "/" + cmd;
        }
        this.cmd = cmd;
    }

    String getCommand() {
        return cmd;
    }

    /**
     * Add a parameter to a command.
     */
    void addParameter(String name, String value) {
        params.add(new Parameter(name, value));
    }

    /**
     * Add a valueless parameter to the command
     */
    void addParameter(Parameter param) {
        params.add(param);
    }

    /**
     * Add a property to include in a result
     */
    void addProperty(String... names) {
        properties.addAll(Arrays.asList(names));
    }

    void addQuery(String... queries) {
        this.queries.addAll(Arrays.asList(queries));
    }

    void setTag(String tag) {
        this.tag = tag;
    }

    List<String> getQueries() {
        return queries;

    }

    String getTag() {
        return tag;
    }

    List<String> getProperties() {
        return properties;
    }

    List<Parameter> getParameters() {
        return params;
    }
    private String cmd;
    private List<Parameter> params = new LinkedList<Parameter>();
    private List<String> queries = new LinkedList<String>();
    private List<String> properties = new LinkedList<String>();
    private String tag;
}
