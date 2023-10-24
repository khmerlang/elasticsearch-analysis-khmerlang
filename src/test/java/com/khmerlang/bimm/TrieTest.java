package com.khmerlang.bimm;

import junit.framework.TestCase;

public class TrieTest  extends TestCase {
    public void testTireSearch() {
        Trie trie = new Trie();
        trie.insert("អ្នក");
        trie.insert("អ្នកចេះ");
        trie.insert("និយាយ");
        trie.insert("ភាសា");
        trie.insert("ខ្មែរ");
        trie.insert("ភាសាខ្មែរ");

        assertEquals(true, trie.searchWord("អ្នក"));
        assertEquals(false, trie.searchWord("អ្ន"));
        assertEquals(true, trie.searchWordPrefix("អ្ន"));
    }

    public void testTireSearchReverse() {
        Trie trie = new Trie();
        trie.insert(reverseString("អ្នក"));
        trie.insert(reverseString("អ្នកចេះ"));
        trie.insert(reverseString("និយាយ"));
        trie.insert(reverseString("ភាសា"));
        trie.insert(reverseString("ខ្មែរ"));
        trie.insert(reverseString("ភាសាខ្មែរ"));

        assertEquals(false, trie.searchWord("អ្នក"));

        assertEquals(true, trie.searchWord("កន្អ"));
        assertEquals(false, trie.searchWord("ះេច"));
        assertEquals(true, trie.searchWordPrefix("ះេច"));
    }

    private String reverseString(String word) {
        StringBuilder tmpStr = new StringBuilder();
        tmpStr.append(word);
        tmpStr.reverse();
        return tmpStr.toString();
    }
}
