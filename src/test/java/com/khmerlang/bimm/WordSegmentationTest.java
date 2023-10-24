package com.khmerlang.bimm;

import junit.framework.TestCase;

public class WordSegmentationTest extends TestCase {

    public void testSegmentSentences() {
        String sentences = "១២៣៤៥៦៧អ្នកចេះ​និយាយភាសាខ្មែរទេ? 1234567អ្នកចេះនិយាយភាសាខ្មែរទេ?";
        WordSegmentation ws = new WordSegmentation("dicts.txt");
        ws.segmentSentences(sentences, false);
        assertEquals("១២៣៤៥៦៧,អ្នកចេះ,និយាយ,ភាសាខ្មែរ,ទេ,1234567,អ្នកចេះ,និយាយ,ភាសាខ្មែរ,ទេ", ws.joinWords(","));
    }

    public void testSegmentSentencesWithPunctuation() {
        String sentences = "១២៣៤៥៦៧អ្នកចេះ​និយាយភាសាខ្មែរទេ? 1234567អ្នកចេះនិយាយភាសាខ្មែរទេ?";
        WordSegmentation ws = new WordSegmentation("dicts.txt");
        ws.segmentSentences(sentences, true);
        assertEquals("១២៣៤៥៦៧,អ្នកចេះ,និយាយ,ភាសាខ្មែរ,ទេ,?,1234567,អ្នកចេះ,និយាយ,ភាសាខ្មែរ,ទេ,?", ws.joinWords(","));
    }

    public void testSegmentSentencesEN() {
        String sentences = "I love Khmerlang.";
        WordSegmentation ws = new WordSegmentation("dicts.txt");
        ws.segmentSentences(sentences, false);
        assertEquals("I,love,Khmerlang,.", ws.joinWords(","));
    }
}
