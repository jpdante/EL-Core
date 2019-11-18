package com.ellisiumx.ELCore.Lang;

import java.util.HashMap;

public class LanguageManager {

    private HashMap<String, LanguageDB> languages;

    public LanguageManager() {
        languages = new HashMap<String, LanguageDB>();
    }

    public void addLanguage(LanguageDB langDB) {
        languages.clear();

    }

    public void removeLanguage(LanguageDB langDB) {
        languages.remove(langDB.Locale);
    }

    public void getLanguage() {

    }

    public String getTranslation(String lang, String msgKey) {
        if(!languages.containsKey(lang)) {
            return languages.get("en_US").getTranslation(msgKey);
        }
        return languages.get(lang).getTranslation(msgKey);
    }

}
