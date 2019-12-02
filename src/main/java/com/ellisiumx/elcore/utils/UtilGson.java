package com.ellisiumx.elcore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class UtilGson {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Gson getGson() {
        return gson;
    }

    public static String serialize(Object object) {
        return gson.toJson(object);
    }

    public static <T> T deserialize(String serializedData, Class<T> type) {
        return gson.fromJson(serializedData, type);
    }

    public static <T> T deserialize(JsonReader reader, Class<T> type) {
        return gson.fromJson(reader, type);
    }
}
