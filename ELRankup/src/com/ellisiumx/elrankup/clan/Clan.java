package com.ellisiumx.elrankup.clan;

public class Clan {

    public int id;
    public int kills;
    public int deaths;
    public String tag;
    public String colorTag;
    public String name;
    public boolean verified;
    public boolean friendFire;

    public Clan(int id, boolean verified, String tag, String colorTag, String name, boolean friendFire, int kills, int deaths) {
        this.id = id;
        this.kills = kills;
        this.deaths = deaths;
        this.tag = tag;
        this.colorTag = colorTag;
        this.name = name;
        this.verified = verified;
        this.friendFire = friendFire;
    }
}
