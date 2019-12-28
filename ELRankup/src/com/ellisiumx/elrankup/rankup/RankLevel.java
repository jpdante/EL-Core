package com.ellisiumx.elrankup.rankup;

public class RankLevel {

    public String name;
    public String tag;
    public boolean canLevelUp;
    public double cost;

    public RankLevel(String name, String tag, double cost, boolean canLevelUp) {
        this.name = name;
        this.tag = tag;
        this.cost = cost;
        this.canLevelUp = canLevelUp;
    }

}
