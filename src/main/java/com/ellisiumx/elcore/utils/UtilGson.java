package com.ellisiumx.elcore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class UtilGson {

    private static Gson prettyGson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    public static Gson getGson() {
        return gson;
    }

    public static String serialize(Object object) {
        return serialize(object, false);
    }

    public static String serialize(Object object, boolean pretty) {
        if(pretty) return prettyGson.toJson(object);
        return gson.toJson(object);
    }

    public static <T> T deserialize(String serializedData, Class<T> type) {
        return deserialize(serializedData, type, false);
    }

    public static <T> T deserialize(JsonReader reader, Class<T> type) {
        return deserialize(reader, type, false);
    }

    public static <T> T deserialize(String serializedData, Class<T> type, boolean pretty) {
        return gson.fromJson(serializedData, type);
    }

    public static <T> T deserialize(JsonReader reader, Class<T> type, boolean pretty) {
        return gson.fromJson(reader, type);
    }
}
