package com.khmerlang.bimm;

import com.khmerlang.utils.WordTermEnum;

import java.util.ArrayList;
import java.util.List;

import static com.khmerlang.utils.KhUtil.*;

public class FMM implements BiMM {
    private List<Word> wordsList;
    private int countNotWord;
    private int startPosIndex = 0;
    private TrieModel trieModel;

    public FMM(String dictPath) {
        trieModel = TrieModel.getInstance(dictPath);
        wordsList = new ArrayList<Word>();
        this.countNotWord = 0;
        this.startPosIndex = 0;
    }

    @Override
    public boolean getStatus() {
        return trieModel.status;
    }

    @Override
    public void segWords(String sentence, int startIndex) {
        this.reset();
        this.startPosIndex = startIndex;
        int index = 0;
        String notWord = "";
        Word word;

        char[] characters = sentence.toCharArray();
        while(index < characters.length) {
            char ch = characters[index];
            if(isIsKhNumber(ch)) {
                word = getKhNumbers(characters, index);
            } else if(isArabicNumber(ch)) {
                word = getNumbers(characters, index);
            } else if(isNotKhmer(ch)) {
                word = getNotKhmer(characters, index);
            } else if(isSymbol(ch)) {
                word = new Word(ch + "", WordTermEnum.SYMBOL, calPos(index), calPos(index));
            } else {
                word = getWord(characters, index);
            }

            int length = word.text.length();
            if(length == 0) {
                notWord += ch;
                index += 1;
                if(index >= characters.length) {
                    wordsList.add(new Word(notWord, WordTermEnum.NOT_WORD, calPos(index - notWord.length()), calPos(index)));
                    this.countNotWord += 1;
                    notWord = "";
                }

                continue;
            }

            if(notWord.length() > 0) {
                wordsList.add(new Word(notWord, WordTermEnum.NOT_WORD, calPos(index - notWord.length()), calPos(index)));
                this.countNotWord += 1;
                notWord = "";
            }

            wordsList.add(word);
            index += length;
        }
    }
    @Override
    public int getCountNotWord() {
        return countNotWord;
    }

    @Override
    public List<Word> getWordsList() {
        return wordsList;
    }

    @Override
    public void reset() {
        wordsList.clear();
        this.countNotWord = 0;
        this.startPosIndex = 0;
    }

    private int calPos(int pos) {
        return startPosIndex + pos;
    }

    private Word getWord(char[] characters, int index) {
        String result = "";
        int currentIndex = index;
        Word word = new Word("", WordTermEnum.KH_WORD, calPos(index), calPos(index));
        while (currentIndex < characters.length) {
            char ch = characters[currentIndex];
            result += ch;
            Node node = trieModel.fmmTrie.searchNode(result);
            if(trieModel.fmmTrie.searchWordPrefix(result)) {
                if (node != null) {
                    word.setValues(result, WordTermEnum.KH_WORD, calPos(index), calPos(currentIndex));
                }
            } else if (node != null) {
                word.setValues(result, WordTermEnum.KH_WORD, calPos(index), calPos(currentIndex));
                return word;
            } else {
                return word;
            }

            currentIndex += 1;
        }

        return word;
    }
    private Word getNotKhmer(char[] characters, int index) {
        String result = "";
        int currentIndex = index;
        while(currentIndex < characters.length) {
            char ch = characters[currentIndex];
            if (isNotKhmer(ch) && !isIsKhNumber(ch) && !isArabicNumber(ch)) {
                result += ch;
                currentIndex +=1;
            } else {
                return new Word(result, WordTermEnum.OTHER, calPos(index), calPos(currentIndex - 1));
            }
        }

        return new Word(result, WordTermEnum.OTHER, calPos(index), calPos(currentIndex - 1));
    }
    private Word getKhNumbers(char[] characters, int index) {
        String result = "";
        int currentIndex = index;
        while(currentIndex < characters.length) {
            char ch = characters[currentIndex];
            if(isIsKhNumber(ch)) {
                result += ch;
                currentIndex +=1;
            } else {
                return new Word(result, WordTermEnum.NUMBER, calPos(index), calPos(currentIndex - 1));
            }
        }

        return new Word(result, WordTermEnum.NUMBER, calPos(index), calPos(currentIndex - 1));
    }

    private Word getNumbers(char[] characters, int index) {
        String result = "";
        int currentIndex = index;
        while(currentIndex < characters.length) {
            char ch = characters[currentIndex];
            if(isArabicNumber(ch)) {
                result += ch;
                currentIndex +=1;
            } else {
                return new Word(result, WordTermEnum.NUMBER, calPos(index), calPos(currentIndex - 1));
            }
        }

        return new Word(result, WordTermEnum.NUMBER, calPos(index), calPos(currentIndex - 1));
    }
}
