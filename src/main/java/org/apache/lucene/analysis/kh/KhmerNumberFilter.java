package org.apache.lucene.analysis.kh;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

import static com.khmerlang.utils.KhUtil.isIsKhNumber;

public class KhmerNumberFilter extends TokenFilter {
    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

    private final PositionIncrementAttribute posIncrAttr = addAttribute(PositionIncrementAttribute.class);
    private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);

    public KhmerNumberFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        if (!keywordAttr.isKeyword()) {
            String term = termAttr.toString();
            boolean numeralTerm = isNumeral(term);
            if(numeralTerm) {
//                posIncrAttr.setPositionIncrement(1);
                String normalizedNumber = normalizeNumber(term);

                termAttr.setEmpty();
                termAttr.append(normalizedNumber);
            }
        }

        return true;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }

    public String normalizeNumber(String number) {
        number = number.replaceAll("១", "1");
        number = number.replaceAll("២", "2");
        number = number.replaceAll("៣", "3");
        number = number.replaceAll("៤", "4");
        number = number.replaceAll("៥", "5");
        number = number.replaceAll("៦", "6");
        number = number.replaceAll("៧", "7");
        number = number.replaceAll("៨", "8");
        number = number.replaceAll("៩", "9");
        number = number.replaceAll("០", "0");
        return  number;

    }

    public boolean isNumeral(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (!isNumeral(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isNumeral(char c) {
        return isIsKhNumber(c);
    }
}
