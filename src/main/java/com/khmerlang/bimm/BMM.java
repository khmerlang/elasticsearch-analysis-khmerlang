package com.khmerlang.bimm;

import com.khmerlang.utils.WordTermEnum;

import java.util.ArrayList;
import java.util.List;
import static com.khmerlang.utils.KhUtil.*;
public class BMM implements BiMM{
    private List<Word> wordsList;
    private int countNotWord;
    private int startPosIndex = 0;
    private TrieModel trieModel;

    public BMM(String dictPath) {
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
        char[] characters = sentence.toCharArray();
        int index = characters.length - 1;
        String notWord = "";
        Word word;

        while (index >= 0) {
            char ch = characters[index];
            if(isIsKhNumber(ch)) {
                word = getKhNumbers(characters, index);
            } else if(isArabicNumber(ch)) {
                word = getNumbers(characters, index);
            } else if(isNotKhmer(ch)) {
                word = getNotKhmer(characters, index);
            } else if(isSymbol(ch)) {
                word = new Word(ch + "", WordTermEnum.OTHER, calPos(index), calPos(index));
            } else {
                word = getWord(characters, index);
            }

            int length = word.text.length();
            if(length == 0) {
                notWord = ch + notWord;
                index -= 1;
                if(index < 0) {
                    wordsList.add(0, new Word(notWord, WordTermEnum.NOT_WORD, calPos(index), (index + notWord.length())));
                    this.countNotWord += 1;
                    notWord = "";
                }

                continue;
            }

            if(notWord.length() > 0) {
                wordsList.add(0, new Word(notWord, WordTermEnum.NOT_WORD, calPos(index), calPos(index + notWord.length())));
                this.countNotWord += 1;
                notWord = "";
            }

            wordsList.add(0, word);
            index -= length;
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
        String resultReverse = "";
        int currentIndex = index;
        Word word = new Word("", WordTermEnum.NAN, calPos(index), calPos(index));
        while (currentIndex >= 0) {
            char ch = characters[currentIndex];
            result = ch + result;
            resultReverse += ch;
            Node node = trieModel.bmmTrie.searchNode(resultReverse);
            if(trieModel.bmmTrie.searchWordPrefix(resultReverse)) {
                if (node != null) {
                    word.setValues(result, WordTermEnum.KH_WORD, calPos(currentIndex), calPos(index));
                }
            } else if (node != null) {
                word.setValues(result, WordTermEnum.KH_WORD, calPos(currentIndex), calPos(index));
                return word;
            } else {
                return word;
            }

            currentIndex -= 1;
        }

        return word;
    }

    private Word getNotKhmer(char[] characters, int index) {
        String result = "";
        int currentIndex = index;
        while(currentIndex >= 0) {
            char ch = characters[currentIndex];
            if (isNotKhmer(ch) && !isIsKhNumber(ch) && !isArabicNumber(ch)) {
                result = ch + result;
                currentIndex -=1;
            } else {
                return new Word(result, WordTermEnum.OTHER, calPos(currentIndex + 1), calPos(index));
            }
        }

        return new Word(result, WordTermEnum.OTHER, calPos(currentIndex + 1), calPos(index));
    }

    private Word getKhNumbers(char[] characters, int index) {
        String result = "";
        int currentIndex = index;
        while(currentIndex >= 0) {
            char ch = characters[currentIndex];
            if(isIsKhNumber(ch)) {
                result = ch + result;
                currentIndex -=1;
            } else {
                return new Word(result, WordTermEnum.NUMBER, calPos(currentIndex + 1), calPos(index));
            }
        }

        return new Word(result, WordTermEnum.NUMBER, calPos(currentIndex + 1), calPos(index));
    }

    private Word getNumbers(char[] characters, int index) {
        String result = "";
        int currentIndex = index;
        while(currentIndex >= 0) {
            char ch = characters[currentIndex];
            if(isArabicNumber(ch)) {
                result = ch + result;
                currentIndex -=1;
            } else {
                return new Word(result, WordTermEnum.NUMBER, calPos(currentIndex + 1), calPos(index));
            }
        }

        return new Word(result, WordTermEnum.NUMBER, calPos(currentIndex + 1), calPos(index));
    }
}
