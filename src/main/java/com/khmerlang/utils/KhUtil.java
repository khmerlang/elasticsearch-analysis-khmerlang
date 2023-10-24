package com.khmerlang.utils;

public final class KhUtil {
    private static String KH_NUM  = "០១២៣៤៥៦៧៨៩,.";
    private static String ARAB_NUM  = "0123456789,.";
    private static String SYMBOL = "។៕៖ៗ៘៙៚៛ៜ៰៱៲៳៴៵៶៷៸៹";
    private static String KH_CHERACTERS = "កខគឃងចឆជឈញដឋឌឍណតថទធនបផពភមយរលវឝឞសហឡអឣឤឥឦឧឨឩឪឫឬឭឮឯឰឱឲឳាិីឹឺុូួើឿៀេែៃោៅំះៈ៉៊់៌៍៎៏័៑្៓។៕៖ៗ៘៙៚៛ៜ៝៰៱៲៳៴៵៶៷៸៹";
    private static String SEPERATORS = "។៕៚៘៙?!៖ \t\n";
    private static String PUNCTUATIONS = "។៕៚៘៙?!៖";

    public static boolean isIsKhNumber(char ch) { return KH_NUM.indexOf(ch) >= 0; }
    public static boolean isArabicNumber(char ch) { return ARAB_NUM.indexOf(ch) >= 0;}

    public static boolean isSymbol(char ch) { return SYMBOL.indexOf(ch) >= 0; }
    public static boolean isNotKhmer(char ch) { return KH_CHERACTERS.indexOf(ch) <= -1; }
    public static boolean isSeperator(char ch) { return SEPERATORS.indexOf(ch) >= 0; }
    public static boolean isStrSeperator(String str) { return str.length() == 1 && PUNCTUATIONS.indexOf(str) >= 0; }
}
