/*
 * Copyright 2014 GideonLeGrange.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.legrange.mikrotik.impl;

/**
 * A simple scanner.
 * @author gideon
 */
class Scanner {
    
    enum Token {
        SLASH("/"), COMMA(","), EOL(), WS, TEXT, 
        LESS("<"), MORE(">"), EQUALS("="),
        WHERE, NOT, AND, OR, RETURN;

        @Override
        public String toString() {
            return (symb == null) ? name() : symb;
        }

        private Token(String symb) {
            this.symb = symb;
        }
        
        private Token() {
            symb = null;
        }
        
        
        private final String symb;
     }
    
    Scanner(String line) {
        this.line = line;
        nextChar();
    }
    
    Token next() throws ScanException {
        text = null;
        switch (c) {
            case '\n' : return Token.EOL;
            case ' '  : 
            case '\t' : 
                return whiteSpace();
            case ',' :
                nextChar();
                return Token.COMMA;
            case '/' : 
                nextChar(); 
                return Token.SLASH;
            case '<' : 
                nextChar(); 
                return Token.LESS;
            case '>' : 
                nextChar(); 
                return Token.MORE;
            case '=' : 
                nextChar(); 
                return Token.EQUALS;
            case '"' : 
                return quotedText();
            default :
                return name();
        }
        
    }
    
    String text()  {
        if (text != null) return text.toString(); 
        return "";
    }
    
    private Token name() throws ScanException {
        text = new StringBuilder();
        while (in(c,"[A-Za-z0-9-\\.]")) {
            text.append(c);
            nextChar();
        }
        String val = text.toString();
        switch  (val) {
            case "where" : return Token.WHERE;
            case "not" : return Token.NOT;
            case "and" : return Token.AND;
            case "or" : return Token.OR;
            case "return" : return Token.RETURN;
        }
        return Token.TEXT;
    }
    
    /** process quoted text */
    private Token quotedText() throws ScanException {
        nextChar(); // eat the '"'
        text = new StringBuilder();
        while (c != '"') {
            if (c == '\n') {
                throw new ScanException("Unclosed quoted text");
            }
            text.append(c);
            nextChar();
        }
        nextChar(); // eat the '"'
        return Token.TEXT;
    }
    
    /** process white space */
    private Token whiteSpace() {
        while ((c == ' ') || (c == '\t')) {
            nextChar();
        }
        return Token.WS;
    }
    
    private void nextChar() {
        if (pos < line.length()) {
           c = line.charAt(pos);
            pos ++;
        }
        else {
            c = '\n';
        }
    }
    
    private boolean in(char c, String cs) {
        return ("" + c).matches(cs);
    }
    
    private final String line;
    private int pos = 0;
    private char c;
    private StringBuilder text;
}
