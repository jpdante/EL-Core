package com.ellisiumx.elcore.lang;

import java.util.HashMap;

public class LanguageDB {

    public final String locale;
    public final String language;
    private HashMap<String, String> translation;

    public LanguageDB() {
        locale = "en_US";
        language = "English";
        translation = new HashMap<String, String>();
    }

    public LanguageDB(String locale, String language) {
        this.locale = locale;
        this.language = language;
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
