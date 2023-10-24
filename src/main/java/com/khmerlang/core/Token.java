package com.khmerlang.core;

import com.khmerlang.utils.WordTermEnum;

import java.util.ArrayList;
import java.util.List;

public final class Token implements Cloneable {
    private final String text;
    private final WordTermEnum type;
    private final int startPos;
    private final int endPos;

    public Token(String text, int start, int end) {
        this(text, WordTermEnum.KH_WORD, start, end);
    }

    public Token cloneWithNewText(String newText, int newEnd) {
        return new Token(newText, type, startPos, endPos);
    }
    public Token(String text, WordTermEnum type, int start, int end) {
        this.text = text;
        this.type = type;
        this.startPos = start;
        this.endPos = end > 0 ? end : start + text.length();
    }

    public String getText() {
        return text;
    }
    public WordTermEnum getType() {
        return type;
    }

    public int getPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public static ArrayList<String> toStringList(List<Token> tokenList) {
        ArrayList<String> temp = new ArrayList<>();
        for (Token token : tokenList) {
            temp.add(token.getText());
        }
        return temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" `").append(text).append('`');
        sb.append(' ').append(startPos).append('-').append(endPos);
        return sb.toString();
    }

    @Override
    public Token clone() {
        return new Token(text, type, startPos, endPos);
    }

    @Override
    public int hashCode() {
        return text.hashCode() ^ type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token that = (Token) obj;

        if (!this.text.equals(that.text)) {
            return false;
        }

        if (this.type != that.type) {
            return false;
        }

        return true;
    }

    public boolean isWord() {
        return type == WordTermEnum.WORD;
    }

    public boolean isPunct() {
        return type == WordTermEnum.PUNCT;
    }

    public boolean isNumber() {
        return type == WordTermEnum.NUMBER;
    }

    public boolean isSpace() {
        return type == WordTermEnum.SPACE;
    }

    public boolean isWordOrNumber() {
        return isWord() || isNumber();
    }
}
