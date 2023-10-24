package org.apache.lucene.analysis.kh;

import org.elasticsearch.analysis.KhmerlangConfig;
import com.khmerlang.core.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

public class KhmerlangTokenizer extends Tokenizer {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    private final KhmerlangTokenizerImpl tokenizer;
    private int offset = 0;

    public KhmerlangTokenizer(KhmerlangConfig config) {
        super();
        tokenizer = new KhmerlangTokenizerImpl(config, input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        final Token token = tokenizer.getNextToken();
        if (token != null) {
            posIncrAtt.setPositionIncrement(1);
            typeAtt.setType(String.format("<%s>", token.getType()));
            termAtt.copyBuffer(token.getText().toCharArray(), 0, token.getText().length());
            offsetAtt.setOffset(correctOffset(token.getPos()), offset = correctOffset(token.getEndPos()));
            return true;
        }
        return false;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        final int finalOffset = correctOffset(offset);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenizer.reset(input);
        offset = 0;
    }
}
