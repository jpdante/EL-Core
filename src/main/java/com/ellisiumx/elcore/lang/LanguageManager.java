package com.ellisiumx.elcore.lang;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.configuration.CoreConfiguration;
import com.ellisiumx.elcore.utils.UtilGson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;

public class LanguageManager {
    private static LanguageManager context;
    private HashMap<String, LanguageDB> languages;

    public LanguageManager() {
        context = this;
        this.languages = new HashMap<>();
        loadLanguages();
    }

    public static void loadLanguages() {
        if(context.languages.size() != 0) return;
        load();
    }

    public static boolean saveLanguages() {
        boolean saved = false;
        for(LanguageDB languageDB : context.languages.values()) {
            if(!languageDB.wasUpdated()) continue;
            FileWriter writer = null;
            try {
                writer = new FileWriter(Paths.get(ELCore.getContext().getDataFolder().getPath(), languageDB.getFilename()).toString(), false);
                writer.write(UtilGson.serialize(languageDB));
                writer.flush();
                saved = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    assert writer != null;
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return saved;
    }

    public static void reloadLanguages() {
        context.languages.clear();
        load();
    }

    private static void load() {
        final File defaultCfg = new File(ELCore.getContext().getDataFolder(), "en_US.json");
        if (!defaultCfg.exists()) {
            defaultCfg.getParentFile().mkdirs();
            ELCore.getContext().saveResource("en_US.json", false);
        }
        for (String filename : CoreConfiguration.Languages) {
            try {
                JsonReader reader = new JsonReader(new FileReader(Paths.get(ELCore.getContext().getDataFolder().getPath(), filename).toString()));
                LanguageDB langDb = UtilGson.deserialize(reader, LanguageDB.class);
                langDb.setFilename(filename);
                addLanguage(langDb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addLanguage(LanguageDB langDB) {
        context.languages.clear();
        context.languages.put(langDB.getLocale(), langDB);
    }

    public static void removeLanguage(LanguageDB langDB) {
        context.languages.remove(langDB.getLocale());
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

    public static Collection<LanguageDB> getLanguages() {
        return context.languages.values();
    }

}
