package com.ellisiumx.elrankup.clan;

import java.util.ArrayList;

public class Clan {

    public int id;
    public int kills;
    public int deaths;
    public int leader;
    public String tag;
    public String colorTag;
    public String name;
    public boolean verified;
    public boolean friendFire;
    public double kdr;
    public ArrayList<String> members;

    public Clan(boolean verified, int leader, String tag, String colorTag, String name, boolean friendFire, int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
        this.leader = leader;
        this.tag = tag;
        this.colorTag = colorTag;
        this.name = name;
        this.verified = verified;
        this.friendFire = friendFire;
        this.kdr = 0.0d;
    }

    public Clan(int id, boolean verified, int leader, String tag, String colorTag, String name, boolean friendFire, int kills, int deaths) {
        this.id = id;
        this.kills = kills;
        this.deaths = deaths;
        this.leader = leader;
        this.tag = tag;
        this.colorTag = colorTag;
        this.name = name;
        this.verified = verified;
        this.friendFire = friendFire;
        this.kdr = 0.0d;
    }

    public void calculateKdr() {
        kdr = (double)kills / (double)deaths;
    }
}
