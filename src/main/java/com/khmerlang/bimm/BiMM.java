package com.khmerlang.bimm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
public interface BiMM {
    public void segWords(String sentence, int startIndex);
    public void reset();
    public int getCountNotWord();
    public boolean getStatus();
    public List<Word> getWordsList();
}
