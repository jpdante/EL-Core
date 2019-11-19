package com.ellisiumx.ELCore.Lang;

import java.util.HashMap;

public class LanguageDB {

    public final String Locale;
    public final String Language;
    private HashMap<String, String> translation;

    public LanguageDB() {
        Locale = "en_US";
        Language = "English";
        translation = new HashMap<String, String>();
    }

    public LanguageDB(String locale, String language) {
        Locale = locale;
        Language = language;
        translation = new HashMap<String, String>();
    }

    public boolean insertTranslation(String key, String value) {
        if(this.translation.containsKey(key)) return false;
        this.translation.put(key, value);
        return true;
    }

    public boolean removeTranslation(String key) {
        if(!this.translation.containsKey(key)) return false;
        this.translation.remove(key);
        return true;
    }

    public String getTranslation(String key) {
        if(!this.translation.containsKey(key)) return "§cmissing var " + key + "§f";
        return this.translation.get(key);
    }

    public String[] getKeys() {
        return (String[]) this.translation.keySet().toArray();
    }

    public String[] getValues() {
        return (String[]) this.translation.values().toArray();
    }
}
