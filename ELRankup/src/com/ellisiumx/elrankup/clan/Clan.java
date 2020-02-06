package com.ellisiumx.elrankup.clan;

import com.ellisiumx.elrankup.configuration.RankupConfiguration;

import java.util.ArrayList;

public class Clan {

    public int id;
    public int neutralKills;
    public int rivalKills;
    public int civilianKills;
    public int deaths;
    public int leader;
    public String tag;
    public String colorTag;
    public String name;
    public boolean friendFire;
    public double kdr;
    public ArrayList<String> members;
    public ArrayList<Clan> allies;
    public ArrayList<Clan> rivals;

    public Clan(int leader, String tag, String colorTag, String name, boolean friendFire, int neutralKills, int rivalKills, int civilianKills, int deaths) {
        this.neutralKills = neutralKills;
        this.rivalKills = rivalKills;
        this.civilianKills = civilianKills;
        this.deaths = deaths;
        this.leader = leader;
        this.tag = tag;
        this.colorTag = colorTag;
        this.name = name;
        this.friendFire = friendFire;
        this.kdr = 0.0d;
        this.allies = new ArrayList<>();
        this.rivals = new ArrayList<>();
    }

    public Clan(int id, int leader, String tag, String colorTag, String name, boolean friendFire, int neutralKills, int rivalKills, int civilianKills, int deaths) {
        this.id = id;
        this.neutralKills = neutralKills;
        this.rivalKills = rivalKills;
        this.civilianKills = civilianKills;
        this.deaths = deaths;
        this.leader = leader;
        this.tag = tag;
        this.colorTag = colorTag;
        this.name = name;
        this.friendFire = friendFire;
        this.kdr = 0.0d;
        this.allies = new ArrayList<>();
        this.rivals = new ArrayList<>();
    }

    public void calculateKdr() {
        double kills = (neutralKills * RankupConfiguration.ClanNeutralKillWeight +
                rivalKills * RankupConfiguration.ClanRivalKillWeight +
                civilianKills * RankupConfiguration.ClanCivilianKillWeight) / 3.0d;
        kdr = kills / (double) deaths;
    }
}
