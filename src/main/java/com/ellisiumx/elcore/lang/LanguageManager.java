package com.ellisiumx.elcore.lang;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.configuration.CoreConfiguration;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class LanguageManager {
    private static LanguageManager context;
    private HashMap<String, LanguageDB> languages;

    public LanguageManager() {
        context = this;
        this.languages = new HashMap<String, LanguageDB>();
        loadLanguages();
    }

    public static void loadLanguages() {
        final File defaultCfg = new File(ELCore.getContext().getDataFolder(), "en_US.json");
        if (!defaultCfg.exists()) {
            defaultCfg.getParentFile().mkdirs();
            ELCore.getContext().saveResource("en_US.json", false);
        }
        final Gson gson = new Gson();
        for (String filename : CoreConfiguration.Languages) {
            try {
                JsonReader reader = new JsonReader(new FileReader(Paths.get(ELCore.getContext().getDataFolder().getPath(), filename).toString()));
                LanguageDB langDb = gson.fromJson(reader, LanguageDB.class);
                addLanguage(langDb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addLanguage(LanguageDB langDB) {
        context.languages.clear();
        context.languages.put(langDB.locale, langDB);
    }

    public static void removeLanguage(LanguageDB langDB) {
        context.languages.remove(langDB.locale);
    }

    public static LanguageDB getLanguage(String lang) {
        if (!context.languages.containsKey(lang)) {
            return context.languages.get("en_US");
        }
        return context.languages.get(lang);
    }

    public static String getTranslation(String lang, String msgKey) {
        if (!context.languages.containsKey(lang)) {
            return context.languages.get("en_US").getTranslation(msgKey);
        }
        return context.languages.get(lang).getTranslation(msgKey);
    }

}
