package com.ellisiumx.elrankup.clan;

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
    public boolean updated;

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
        this.updated = false;
    }
}
