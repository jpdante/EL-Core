package com.ellisiumx.ELCore.Lang;

public class LanguageDB {

    public final String Locale;

    public LanguageDB() {
        Locale = "en_US";
    }

    public LanguageDB(String locale) {
        Locale = locale;
    }

    public String getTranslation(String msgKey) {
        return "";
    }

}
