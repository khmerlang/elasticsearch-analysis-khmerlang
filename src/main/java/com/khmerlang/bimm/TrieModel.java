package com.khmerlang.bimm;

import com.khmerlang.utils.FileResourcesUtils;

import java.util.List;

public class TrieModel {
    private static TrieModel single_instance = null;
    public Trie fmmTrie;
    public Trie bmmTrie;
    public boolean status;

    private TrieModel(String dictPath) {
        fmmTrie = new Trie();
        bmmTrie = new Trie();
        List<String> wordsDist = FileResourcesUtils.readLinesFromResource(dictPath);
        status = !wordsDist.isEmpty();
        for(String word: wordsDist) {
            fmmTrie.insert(word);
            bmmTrie.insert(reverseString(word));
        }
    }

    private String reverseString(String word) {
        StringBuilder tmpStr = new StringBuilder();
        tmpStr.append(word);
        tmpStr.reverse();
        return tmpStr.toString();
    }

    public static TrieModel getInstance(String dictPath) {
        if (single_instance == null)
            single_instance = new TrieModel(dictPath);

        return single_instance;
    }
}
