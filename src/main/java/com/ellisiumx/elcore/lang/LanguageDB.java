package com.ellisiumx.elcore.lang;

import java.util.HashMap;

public class LanguageDB {

    public final String locale;
    public final String language;
    public final HashMap<String, String> translations;

    public LanguageDB() {
        this.locale = "en_US";
        this.language = "English";
        this.translations = new HashMap<String, String>();
    }

    public LanguageDB(String locale, String language) {
        this.locale = locale;
        this.language = language;
        this.translations = new HashMap<String, String>();
    }

    public boolean insertTranslation(String key, String value) {
        if(this.translations.containsKey(key)) return false;
        this.translations.put(key, value);
        return true;
    }

    public boolean removeTranslation(String key) {
        if(!this.translations.containsKey(key)) return false;
        this.translations.remove(key);
        return true;
    }

    public String getTranslation(String key) {
        if(!this.translations.containsKey(key)) return "missing var " + key;
        return this.translations.get(key);
    }

    public String[] getKeys() {
        return (String[]) this.translations.keySet().toArray();
    }

    public String[] getValues() {
        return (String[]) this.translations.values().toArray();
    }
}
