package org.apache.lucene.analysis.kh;

import com.google.common.io.CharStreams;
import org.elasticsearch.analysis.KhmerlangConfig;
import com.khmerlang.core.Token;
import com.khmerlang.core.Tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class KhmerlangTokenizerImpl {
    private final KhmerlangConfig config;
    private final Tokenizer tokenizer;
    private final List<Token> pending;
    private Reader input;
    private int pos = -1;

    KhmerlangTokenizerImpl(KhmerlangConfig config, Reader input) {
        this.config = config;
        this.input = input;
        tokenizer = AccessController.doPrivileged(
                (PrivilegedAction<Tokenizer>) () -> Tokenizer.getInstance(config.dictPath)
        );
        pending = new CopyOnWriteArrayList<>();
    }

    public Token getNextToken() throws IOException {
        while (pending.size() == 0) {
            tokenize();
            if (pending.size() == 0) {
                return null;
            }
        }
        pos++;
        return pos < pending.size() ? pending.get(pos) : null;
    }

    public void reset(Reader input) {
        this.input = input;
        pending.clear();
        pos = -1;
    }

    private void tokenize() throws IOException {
        final List<Token> tokens = tokenize(input);
        if (tokens != null) {
            pending.addAll(tokens);
        }
    }

    private List<Token> tokenize(Reader input) throws IOException {
        return tokenize(CharStreams.toString(input));
    }

    private List<Token> tokenize(String input) {
        return AccessController.doPrivileged(
                (PrivilegedAction<List<Token>>) () -> tokenizer.segment(input, config)
        );
    }
}