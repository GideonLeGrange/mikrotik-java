package me.legrange.mikrotik.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import me.legrange.mikrotik.impl.Scanner.Token;

/**
 * Parse the pseudo-command line into command objects. 
 * @author GideonLeGrange
 */
class Parser {

    public static void main(String...args) throws Exception { 
     /*   System.out.println(parse("/system/reboot"));
        System.out.println(parse("/interface/print where type=ether"));
        System.out.println(parse("/interface/wireless/monitor .id=wlan1"));
        System.out.println(parse("/ip/firewall/filter/add action=drop chain=forward in-interface=gre1 protocol=udp dst-port=78,80"));
        System.out.println(parse("/file/print detail where name=conf.rsc"));
        System.out.println(parse("/ip/firewall/filter/add action=drop chain=forward in-interface=aps_hs protocol=udp dst-port=78,80,32")); */ 
        System.out.println(parse("/interface/gre/set .id=gre1 comment=\"test coment\""));
    }
    /** parse the given bit of text into a Command object */
    static Command parse(String text) throws ParseException {
        Parser parser = new Parser(text);
        return parser.parse();
    }

    /** run parse on the internal data and return the command object */
    private Command parse() throws ParseException {
        command();
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
    
    private void command() throws ParseException {
        StringBuilder path = new StringBuilder();
        do {
            expect(Token.SLASH);
            path.append("/");
            next();
            expect(Token.TEXT);
            path.append(text);
            next();
        } while (token == Token.SLASH);
        cmd = new Command(path.toString());
    }

    private void param() throws ParseException {
        String name = text;
        next();
        if (token == Token.EQUALS) {
            next();
            expect(Token.TEXT);
            StringBuilder val = new StringBuilder(text);
            next();
            while (token == Token.COMMA) {
                val.append(",");
                next();
                expect(Token.TEXT);
                val.append(text);
                next();
            }
            cmd.addParameter(new Parameter(name, val.toString()));
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
        expect(Token.NOT, Token.TEXT);
        switch (token) {
            case NOT:
                notExpr();
                break;
            case TEXT: {
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

    private void eqExpr(String name) throws  ParseException {
        next(); // eat = 
        expect(Token.TEXT);
        cmd.addQuery(String.format("?%s=%s", name, text));
        next();
    }

    private void lessExpr(String name) throws ScanException {
        next(); // eat < 
        cmd.addQuery(String.format("?<%s=%s", name, text));
        next();
    }

    private void moreExpr(String name) throws ScanException {
        next(); // eat >
        cmd.addQuery(String.format("?>%s=%s", name, text));
        next();
    }

    private void hasExpr(String name) {
        cmd.addQuery(String.format("?%s", name));
    }

    private void returns() throws ParseException {
        next();
        expect(Token.TEXT);
        List<String> props = new LinkedList<>();
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
        throw new ParseException(String.format("Expected %s but found %s at position %d", Arrays.asList(tokens), this.token, scanner.pos()));
    }

    /** move to the next token returned by the scanner */
    private void next() throws ScanException {
        token = scanner.next();
        while (token == Token.WS) {
            token = scanner.next();
        }
        text = scanner.text();
    }

    private Parser(String line) throws ScanException {
        line = line.trim();
        scanner = new Scanner(line);
        next();
    }
    
    
    
    private final Scanner scanner;
    private Token token;
    private String text;
    private Command cmd;

}
