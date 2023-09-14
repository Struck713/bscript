package com.noah.bscript.lang;

import com.noah.bscript.utils.CharacterUtils;

import java.util.ArrayList;
import java.util.List;

public class BLexer {

    private BScript script;
    private String source;
    private List<BToken> tokens;

    private int start;
    private int current;
    private int line;

    public BLexer(BScript script, String source) {
        this.script = script;
        this.source = source;
        this.tokens = new ArrayList<>();
    }

    /**
     * Tokenize the provided source with the BScript.
     */
    public List<BToken> tokenize() {
        this.tokens.clear();

        while (!this.isEnd()) {
            this.start = this.current;

            char next = this.advance();
            switch (next) {

                // one liners
                case '(': this.add(BToken.Type.LEFT_PAREN); break;
                case ')': this.add(BToken.Type.RIGHT_PAREN); break;
                case '{': this.add(BToken.Type.LEFT_BRACE); break;
                case '}': this.add(BToken.Type.RIGHT_BRACE); break;
                case ',': this.add(BToken.Type.COMMA); break;
                case '.': this.add(BToken.Type.DOT); break;
                case '-': this.add(BToken.Type.MINUS); break;
                case '+': this.add(BToken.Type.PLUS); break;
                case ';': this.add(BToken.Type.SEMICOLON); break;
                case '*': this.add(BToken.Type.STAR); break;
                case '!': this.add(this.expect('=') ? BToken.Type.NOT_EQUAL : BToken.Type.NOT); break;
                case '=': this.add(this.expect('=') ? BToken.Type.EQUAL_EQUAL : BToken.Type.EQUAL); break;
                case '<': this.add(this.expect('=') ? BToken.Type.LESS_EQUAL : BToken.Type.LESS); break;
                case '>': this.add(this.expect('=') ? BToken.Type.GREATER_EQUAL : BToken.Type.GREATER); break;

                case '/':
                    if (this.expect('/')) {
                        while (this.peek() != CharacterUtils.LINE_FEED && !isEnd()) this.advance();
                    } else if (this.expect('*')) {
                        while (!(this.peek() == '*' && this.peekNext() == '/') && !isEnd()) this.advance();

                        if (isEnd()) {
                            this.script.error(this.line, " at end", "Block did not terminate.");
                            break;
                        }

                        // eat the */
                        this.eat(2);
                    } else
                        this.add(BToken.Type.SLASH);
                    break;

                case '"':
                    while (this.peek() != '"' && !isEnd()) {
                        if (this.peek() == CharacterUtils.LINE_FEED) this.line++;
                        this.advance();
                    }

                    if (isEnd()) {
                        this.script.error(this.line, " at end", "String did not terminate.");
                        break;
                    }

                    // eat the "
                    this.eat(1);

                    String value = this.source.substring(this.start + 1, this.current - 1);
                    this.add(BToken.Type.STRING, value);
                    break;

                // ignore whitespace
                case CharacterUtils.SPACE:
                case CharacterUtils.TAB:
                case CharacterUtils.CARRIAGE_RETURN: break;
                case CharacterUtils.LINE_FEED: this.line++; break;

                default:
                    if (Character.isDigit(next)) {
                        while (Character.isDigit(this.peek())) this.advance();
                        if (this.peek() == '.' && Character.isDigit(this.peekNext())) {
                            this.eat(1); // eat the .
                            while (Character.isDigit(this.peek())) this.advance();
                        }

                        this.add(BToken.Type.NUMBER, Double.parseDouble(this.source.substring(this.start, this.current)));
                        break;
                    }

                    else if (CharacterUtils.isAlphanumeric(next)) {
                        while (CharacterUtils.isAlphanumeric(this.peek())) this.advance();

                        String text = source.substring(this.start, this.current);
                        BToken.Type type = BToken.Type.getByText(text);
                        if (type == null) type = BToken.Type.IDENTIFIER;
                        this.add(type, this.source.substring(this.start, this.current));
                        break;
                    }

                    else this.script.error(this.line, "Could not identify token: " + next);
             }
        }

        this.tokens.add(new BToken(BToken.Type.EOF, "<EOF>", null, this.line));
        return this.tokens;
    }

    /**
     * Advance to the next character
     *
     * @return the next character
     */
    private char advance() {
        return this.source.charAt(this.current++);
    }

    /**
     * Eat a certain amount of characters
     *
     * @param amount the amount of characters to eat
     */
    private void eat(int amount) {
        this.current += amount;
    }

    /**
     * Expect a character after the current
     *
     * @param expected the expected character to be next
     * @return if the character was there or not
     */
    private boolean expect(char expected) {
        if (this.isEnd()) return false;
        if (this.source.charAt(this.current) != expected) return false;

        this.current++;
        return true;
    }

    /**
     * Peek the character without moving the current
     *
     * @return the current character
     */
    private char peek() {
        if (this.isEnd()) return CharacterUtils.NULL_TERMINATOR;
        return this.source.charAt(this.current);
    }

    /**
     * Peek the next character without moving the current
     *
     * @return the next character
     */
    private char peekNext() {
        if (this.current + 1 >= this.source.length()) return CharacterUtils.NULL_TERMINATOR;
        return this.source.charAt(this.current + 1);
    }

    /**
     * Add a token to the list of tokens with no literal
     *
     * @param type the {@link BToken.Type}
     */
    private void add(BToken.Type type) {
        this.add(type, null);
    }

    /**
     * Add a token to the list of tokens with a literal
     *
     * @param type the {@link BToken.Type}
     * @param literal the literal Object
     */
    private void add(BToken.Type type, Object literal) {
        String text = this.source.substring(this.start, this.current);
        this.tokens.add(new BToken(type, text, literal, this.line));
    }

    /**
     * @return if we have reached the end of the source or not
     */
    private boolean isEnd() {
        return this.current >= this.source.length();
    }

    public void print() {
        System.out.println(String.format("Lexed %d tokens:", this.tokens.size()));
        for (BToken token : this.tokens) {
            System.out.println(String.format(" :: [%s] %s - %s", token.getType().name(), token.getLexeme(), token.getLiteral() != null ? token.getLiteral().toString() : "NULL"));
        }
    }

}
