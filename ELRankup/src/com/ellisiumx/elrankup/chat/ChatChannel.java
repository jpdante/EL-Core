package com.ellisiumx.elrankup.chat;

public class ChatChannel {
    public String key;
    public String tag;
    public String format;
    public int distance;
    public boolean multiWorld;
    public double minPrice;

    public ChatChannel(String key, String tag, String format, int distance, boolean multiWorld, double minPrice) {
        this.key = key;
        this.tag = tag;
        this.format = format;
        this.distance = distance;
        this.multiWorld = multiWorld;
        this.minPrice = minPrice;
    }
}