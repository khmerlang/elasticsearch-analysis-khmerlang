package org.elasticsearch.analysis;

import org.elasticsearch.common.settings.Settings;

public class KhmerlangConfig {
    public static final String DEFAULT_DICT_PATH = "dicts.txt";
    public static final String DEFAULT_SYNONYM_PATH = "synonym.txt";
    public final String dictPath;
    public final Boolean keepPunctuation;

    public final Boolean correctCharacter;
    public final Boolean lowercase;

    public final Boolean khmerNumber;

    public final Boolean enableStopwords;
    public final Boolean enableSynonym;

    public KhmerlangConfig(Settings settings) {
        dictPath = settings.get("dict_path", DEFAULT_DICT_PATH);
        keepPunctuation = settings.getAsBoolean("keep_punctuation", Boolean.FALSE);
        correctCharacter = settings.getAsBoolean("correct_character", Boolean.TRUE);
        khmerNumber = settings.getAsBoolean("khmer_number", Boolean.FALSE);
        lowercase = settings.getAsBoolean("lowercase", Boolean.FALSE);
        enableStopwords = settings.getAsBoolean("enable_stopwords", Boolean.FALSE);
        enableSynonym = settings.getAsBoolean("enable_synonym", Boolean.FALSE);
    }
}
