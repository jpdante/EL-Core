package com.ellisiumx.elrankup.rankup;

public class RankLevel {

    public String name;
    public String tag;
    public String color;
    public boolean canLevelUp;
    public double cost;

    public RankLevel(String name, String tag, String color, double cost, boolean canLevelUp) {
        this.name = name;
        this.tag = tag;
        this.color = color;
        this.cost = cost;
        this.canLevelUp = canLevelUp;
    }

}
