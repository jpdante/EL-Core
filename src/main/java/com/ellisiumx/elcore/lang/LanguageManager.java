package com.ellisiumx.elcore.lang;

import java.util.HashMap;

public class LanguageManager {
    private static LanguageManager context;
    private HashMap<String, LanguageDB> languages;

    public LanguageManager() {
        context = this;
        this.languages = new HashMap<String, LanguageDB>();
    }

    public static void addLanguage(LanguageDB langDB) {
        context.languages.clear();
        context.languages.put(langDB.locale, langDB);
    }

    public static void removeLanguage(LanguageDB langDB) {
        context.languages.remove(langDB.locale);
    }

    public static LanguageDB getLanguage(String lang) {
        if(!context.languages.containsKey(lang)) {
            return context.languages.get("en_US");
        }
        return context.languages.get(lang);
    }

    public static String getTranslation(String lang, String msgKey) {
        if(!context.languages.containsKey(lang)) {
            return context.languages.get("en_US").getTranslation(msgKey);
        }
        return context.languages.get(lang).getTranslation(msgKey);
    }

}
