package com.noah.bscript.lang;

import com.noah.bscript.exceptions.BScriptException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BScript {

    private File file;
    private List<BToken> tokens;

    public BScript(File file) {
        this.file = file;
        this.tokens = new ArrayList<>();
    }

    /**
     * Load the tokens of the tree
     */
    public void load() {
        this.tokens.clear();

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines().forEach(line -> builder.append(line).append(" "));
        } catch (IOException e) {
            throw new BScriptException("Failed to load BScript: " + this.file);
        }

        this.tokenize(builder.toString());
    }

    /**
     * Tokenize a single line of the script;
     *
     * @param body the line to tokenize
     */
    public void tokenize(String body) {
        int currentPosition = 0;
        while (currentPosition < body.length()) {
            int tokenStartPosition = currentPosition;
            char token = body.charAt(currentPosition);

            // WHITESPACE
            if (Character.isWhitespace(token)) currentPosition++;

            // NUMBERS
            else if (Character.isDigit(token)) {
                StringBuilder numberBuilder = new StringBuilder();
                while (currentPosition < body.length() && Character.isDigit(token)) {
                    token = body.charAt(currentPosition);
                    numberBuilder.append(token);
                    currentPosition++;
                }
                this.tokens.add(new BToken(BType.NUMBER, numberBuilder.toString(), tokenStartPosition));
            }

            // LOGICAL TOKENS
            else if (BType.decodeLogicalType(token) != null) {
                BType type = BType.decodeLogicalType(token);
                this.tokens.add(new BToken(type, Character.toString(token), tokenStartPosition));
                currentPosition++;
            }

            // OTHER (true, false, strings)
            else if (Character.isAlphabetic(token)) {
                StringBuilder stringBuilder = new StringBuilder();
                while (currentPosition < body.length() && Character.isAlphabetic(token)) {
                    token = body.charAt(currentPosition);
                    stringBuilder.append(token);
                    currentPosition++;
                }

                BType type = BType.IDENTIFIER;
                String string = stringBuilder.toString();
                if (string.equals("true")) type = BType.TRUE;
                else if (string.equals("false")) type = BType.FALSE;
                this.tokens.add(new BToken(type, string, tokenStartPosition));
            }

            // FAILED
            else {
                System.out.printf("Encountered invalid token '%c' at %d", token, tokenStartPosition);
                System.out.println();
            }
        }

        this.tokens.add(new BToken(BType.EOF, "<EOF>", currentPosition));
    }

    public void print() {
        System.out.println("TOKENS: " + this.tokens.size());
        for (BToken token : this.tokens) {
            System.out.println("[" + token.getType() + "@" + token.getPosition() + "] " + token.getText());
        }
    }

}
