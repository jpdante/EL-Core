package com.ellisiumx.elrankup.clan;

import com.ellisiumx.elrankup.configuration.RankupConfiguration;

import java.sql.Timestamp;

public class ClanPlayer {

    public int id;
    public int accountId;
    public Clan clan;
    public boolean friendlyFire;
    public int neutralKills;
    public int rivalKills;
    public int civilianKills;
    public int deaths;
    public double kdr;
    public Timestamp lastSeen;
    public Timestamp joinDate;
    public boolean isClanMod;
    public String rank;

    public ClanPlayer(int accountId, Clan clan, boolean friendlyFire, int neutralKills, int rivalKills, int civilianKills, int deaths, Timestamp lastSeen, Timestamp joinDate, boolean isClanMod, String rank) {
        this.accountId = accountId;
        this.clan = clan;
        this.friendlyFire = friendlyFire;
        this.neutralKills = neutralKills;
        this.rivalKills = rivalKills;
        this.civilianKills = civilianKills;
        this.deaths = deaths;
        this.lastSeen = lastSeen;
        this.joinDate = joinDate;
        this.isClanMod = isClanMod;
        this.rank = rank;
    }

    public ClanPlayer(int id, int accountId, Clan clan, boolean friendlyFire, int neutralKills, int rivalKills, int civilianKills, int deaths, Timestamp lastSeen, Timestamp joinDate, boolean isClanMod, String rank) {
        this.id = id;
        this.accountId = accountId;
        this.clan = clan;
        this.friendlyFire = friendlyFire;
        this.neutralKills = neutralKills;
        this.rivalKills = rivalKills;
        this.civilianKills = civilianKills;
        this.deaths = deaths;
        this.lastSeen = lastSeen;
        this.joinDate = joinDate;
        this.isClanMod = isClanMod;
        this.rank = rank;
    }

    public void calculateKdr() {
        double kills = (neutralKills * RankupConfiguration.ClanNeutralKillWeight +
                rivalKills * RankupConfiguration.ClanRivalKillWeight +
                civilianKills * RankupConfiguration.ClanCivilianKillWeight) / 3.0d;
        kdr = kills / (double) deaths;
    }

}