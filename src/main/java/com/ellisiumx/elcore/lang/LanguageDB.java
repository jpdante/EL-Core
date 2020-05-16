package com.ellisiumx.elcore.lang;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LanguageDB {

    private String filename;
    private String locale;
    private String language;
    private HashMap<String, String> translations;
    @Expose(serialize = false)
    private boolean updated = false;

    public LanguageDB() {
        this.locale = "en_US";
        this.language = "English";
        this.translations = new HashMap<>();
        this.updated = false;
    }

    public LanguageDB(String locale, String language) {
        this.locale = locale;
        this.language = language;
        this.translations = new HashMap<>();
        this.updated = false;
    }

    public boolean insertTranslation(String key, String value) {
        if(this.translations.containsKey(key)) return false;
        this.updated = true;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.updated = true;
        this.locale = locale;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.updated = true;
        this.language = language;
    }

    public boolean wasUpdated() {
        return this.updated;
    }

    public void resetUpdate() {
        this.updated = false;
    }
}
