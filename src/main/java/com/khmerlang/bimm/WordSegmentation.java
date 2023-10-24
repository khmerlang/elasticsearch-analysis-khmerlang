package com.khmerlang.bimm;

import com.khmerlang.utils.WordTermEnum;

import java.util.*;

import static com.khmerlang.utils.KhUtil.isSeperator;
import static com.khmerlang.utils.KhUtil.isStrSeperator;

public class WordSegmentation {
    BiMM fmm;
    BiMM bmm;

    public List<Word> wordsList;

    public WordSegmentation(String dictPath) {
        wordsList = new ArrayList<Word>();
        fmm = new FMM(dictPath);
        bmm = new BMM(dictPath);
    }

    public boolean getStatus() {
        return fmm.getStatus() && bmm.getStatus();
    }

    public String joinWords(String delimiter) {
        List<String> words = new ArrayList<>();
        for(Word word: wordsList) {
            words.add(word.text);
        }
        return String.join(delimiter, words);
    }
    public void segmentSentences(String sentences, boolean keepPunctuation) {
        this.reset();

        char[] chars = sentences.toCharArray();
        String sentence = "";
        LinkedHashMap<Integer, String> sentencesDict = new LinkedHashMap<>();

        int index = 0;
        int currentSenIndex = 0;
        for(char ch: chars) {
            if(ch == '​') {
                index += 1;
                continue;
            }

            if(isSeperator(ch)) {
                sentencesDict.put(currentSenIndex, sentence);
                if(isStrSeperator(ch + "") && keepPunctuation) {
                    sentencesDict.put(index, ch + "");
                }

                sentence = "";
                currentSenIndex = index + 1;
            } else {
                sentence += ch;
            }

            index += 1;

        }
        if(!sentence.isEmpty()) {
            sentencesDict.put(currentSenIndex, sentence);
        }

//       TODO: add multithreading
        for( int key : sentencesDict.keySet()) {
            if(isStrSeperator(sentencesDict.get(key))) {
                wordsList.add(new Word(sentencesDict.get(key), WordTermEnum.PUNCT, key, key));
            } else {
                segmentSentence(sentencesDict.get(key), key);
            }
        }
    }

    private void reset() {
        wordsList.clear();
    }

    private void segmentSentence(String sentence, int startIndex) {
        fmm.segWords(sentence, startIndex);
        bmm.segWords(sentence, startIndex);
        if(fmm.getCountNotWord() <= bmm.getCountNotWord()) {
            wordsList.addAll(fmm.getWordsList());
        } else {
            wordsList.addAll(bmm.getWordsList());
        }
    }
}
