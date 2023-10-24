package com.khmerlang.core;

import com.khmerlang.bimm.Word;
import com.khmerlang.bimm.WordSegmentation;
import com.khmerlang.utils.CorrectCharacter;
import org.elasticsearch.analysis.KhmerlangConfig;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private WordSegmentation wsKhmer;
    private static String dictPath = null;

    private static final class Loader {
        private static final Tokenizer INSTANCE = get();

        private Loader() {
        }

        private static Tokenizer get() {
            return new Tokenizer(dictPath);
        }
    }

    public static Tokenizer getInstance(String dictPath) {
        Tokenizer.dictPath = dictPath;
        return Loader.INSTANCE;
    }

    private Tokenizer(String dictPath) {
        wsKhmer = new WordSegmentation(dictPath);
        if (!wsKhmer.getStatus()) {
            throw new RuntimeException(String.format("Cannot initialize Tokenizer: %s", dictPath));
        }
    }

    public List<Token> segment(String text, KhmerlangConfig config) {
        if (text == null) {
            throw new IllegalArgumentException("text is null");
        }


        final List<Token> tokens = new ArrayList<>();
        if(config.correctCharacter) {
            text = CorrectCharacter.clean(text);
        }

        wsKhmer.segmentSentences(text, config.keepPunctuation);
        for(Word word : wsKhmer.wordsList) {
            tokens.add(new Token(word.text, word.term, word.startPos, word.endPos));
        }

        return tokens;
    }
}
