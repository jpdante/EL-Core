package com.ellisiumx.elrankup.clan;

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
    public Timestamp lastSeen;
    public Timestamp joinDate;

    public ClanPlayer(int accountId, Clan clan, boolean friendlyFire, int neutralKills, int rivalKills, int civilianKills, int deaths, Timestamp lastSeen, Timestamp joinDate) {
        this.accountId = accountId;
        this.clan = clan;
        this.friendlyFire = friendlyFire;
        this.neutralKills = neutralKills;
        this.rivalKills = rivalKills;
        this.civilianKills = civilianKills;
        this.deaths = deaths;
        this.lastSeen = lastSeen;
        this.joinDate = joinDate;
    }

    public ClanPlayer(int id, int accountId, Clan clan, boolean friendlyFire, int neutralKills, int rivalKills, int civilianKills, int deaths, Timestamp lastSeen, Timestamp joinDate) {
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
    }

}