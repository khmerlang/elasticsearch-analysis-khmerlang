package com.khmerlang.bimm;

import com.khmerlang.utils.WordTermEnum;

public class Word {
    public String text;
    public WordTermEnum term;
    public int startPos;
    public int endPos;

    public Word() {
        this.text = "";
        this.term = WordTermEnum.NAN;
        this.startPos = 0;
        this.endPos = 0;
    }

    public Word(String text, WordTermEnum term, int startPos, int endPos) {
        this.text = text;
        this.term = term;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public void setValues(String text, WordTermEnum term, int startPos, int endPos) {
        this.text = text;
        this.term = term;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String toString() {
        return text + " " + term + " " + startPos + " " + endPos;
    }
}
