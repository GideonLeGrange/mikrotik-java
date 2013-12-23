package me.legrange.mikrotik.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Parse the pseudo-command line into command objects. 
 * @author GideonLeGrange
 */
class Parser {

    /** parse the given bit of text into a Command object */
    static Command parse(String text) throws ParseException {
        Parser parser = new Parser(text);
        return parser.parse();
    }

    /** run parse on the internal data and return the command object */
    private Command parse() throws ParseException {
        next();
        expect(Token.COMMAND);
        cmd = new Command(text);
        next();
        while (!((token == Token.WHERE) || (token == Token.RETURN) || (token == Token.EOL))) {
            param();
        }
        if (token == Token.WHERE) {
            where();
        }
        if (token == Token.RETURN) {
            returns();
        }
        expect(Token.EOL);
        return cmd;
    }

    private void param() throws ParseException {
        String name = text;
        next();
        if (token == Token.EQUALS) {
            next();
            expect(Token.NAME);
            cmd.addParameter(new Parameter(name, text));
            next();
        }
        else {
            cmd.addParameter(new Parameter(name));
        }
    }

    private void where() throws ParseException {
        next(); // swallow the word "where"
        expr();
    }

    private void expr() throws ParseException {
        expect(Token.NOT, Token.NAME);
        switch (token) {
            case NOT:
                notExpr();
                break;
            case NAME: {
                String name = text;
                next();
                expect(Token.EQUALS, Token.LESS, Token.MORE);
                switch (token) {
                    case EQUALS:
                        eqExpr(name);
                        break;
                    case LESS:
                        lessExpr(name);
                        break;
                    case MORE:
                        moreExpr(name);
                        break;
                    default:
                        hasExpr(name);
                }
            }
            break;
        }
        // if you get here, you had a expression, see if you want more. 
        switch (token) {
            case AND : andExpr();
                break;
            case OR : orExpr();
        }
    }
    
    private void andExpr() throws ParseException {
        next(); // eat and
        expr();
        cmd.addQuery("?#&");
    }
    
      private void orExpr() throws ParseException {
        next(); // eat or
        expr();
        cmd.addQuery("?#|");
    }


    private void notExpr() throws ParseException {
        next(); // eat not
        expr();
        cmd.addQuery("?#!");
    }

    private void eqExpr(String name) {
        next(); // eat = 
        cmd.addQuery(String.format("?%s=%s", name, text));
        next();
    }

    private void lessExpr(String name) {
        next(); // eat < 
        cmd.addQuery(String.format("?<%s=%s", name, text));
        next();
    }

    private void moreExpr(String name) {
        next(); // eat >
        cmd.addQuery(String.format("?>%s=%s", name, text));
        next();
    }

    private void hasExpr(String name) {
        cmd.addQuery(String.format("?%s", name));
    }

    private void returns() throws ParseException {
        next();
        expect(Token.NAME);
        List<String> props = new LinkedList<String>();
        while (!(token == Token.EOL)) {
            if (token != Token.COMMA) {
                props.add(text);
            }
            next();
        }
        cmd.addProperty(props.toArray(new String[]{}));
    }

    private void expect(Token...tokens) throws ParseException {
        for (Token want : tokens) {
            if (this.token == want) return;
        }
        throw new ParseException(String.format("Expected %s but found %s", Arrays.asList(tokens), this.token));
    }

    private void next() {
        if (!words.isEmpty()) {
            text = words.remove(0);
            if (text.startsWith("/")) {
                token = Token.COMMAND;
            } else {
                Token t = lookup.get(text);
                if (t != null) {
                    token = t;
                    text = "";
                } else {
                    token = Token.NAME;
                }
            }
        } else {
            token = Token.EOL;
            text = "";
        }
       // System.out.printf("%s: %s\n", token, text);
    }

    private Parser(String text) {
        text = text.trim();
        StringTokenizer st = new StringTokenizer(text, " \t,=", true);
        while (st.hasMoreElements()) {
            String t = st.nextToken().trim();
            if (!t.equals("")) {
                words.add(t);
            }
        }
    }
    private final List<String> words = new LinkedList<String>();
    private String text;
    private Token token;
    private Command cmd;
    private static final Map<String, Token> lookup = new HashMap<String, Token>();

    private enum Token {

        COMMAND, WHERE, RETURN, EOL, NOT, AND, OR, NAME, EQUALS, MORE, LESS, COMMA;
    }

    static {
        lookup.put("where", Token.WHERE);
        lookup.put("return", Token.RETURN);
        lookup.put("not", Token.NOT);
        lookup.put("and", Token.AND);
        lookup.put("or", Token.OR);
        lookup.put("=", Token.EQUALS);
        lookup.put(">", Token.MORE);
        lookup.put("<", Token.LESS);
        lookup.put(",", Token.COMMA);
    }
}
