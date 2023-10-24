package com.khmerlang.bimm;

import java.util.HashMap;

public class Trie {
    private Node root;

    public Trie() {
        root = new Node();
    }
    public void insert(String word) {
        /* Find the children of root node */
        HashMap<Character, Node> children = root.children;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            Node node = null;
            if (children.containsKey(character)) {
                node = children.get(character);
            } else {
                node = new Node(character);
                children.put(character, node);
            }
            children = node.children;
            if (i == word.length() - 1) {
                node.isLeaf = true;
            }
        }
    }

    public boolean startsWith(String prefix) {
        if (searchNode(prefix) != null) {
            return true;
        }
        return false;
    }

    public Node searchNode(String word) {
        HashMap<Character, Node> children = root.children;
        Node node = null;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            if (children.containsKey(character)) {
                node = children.get(character);
                children = node.children;
            } else {
                return null;
            }
        }
        return node;
    }

    public boolean searchWord(String word) {
        Node node = searchNode(word);
        if (null != node && node.isLeaf) {
            return true;
        }
        return false;
    }

    public boolean searchWordPrefix(String word) {
        Node node = searchNode(word);
        return null != node && !node.children.isEmpty();
    }
}