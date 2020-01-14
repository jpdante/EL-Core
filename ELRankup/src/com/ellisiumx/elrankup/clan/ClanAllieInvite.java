package com.ellisiumx.elrankup.clan;

public class ClanAllieInvite {

    private Clan from;
    private Clan to;
    private int timeout;

    public ClanAllieInvite(Clan from, Clan to, int timeout) {
        this.from = from;
        this.to = to;
        this.timeout = timeout;
    }

    public Clan getFrom() {
        return from;
    }

    public Clan getTo() {
        return to;
    }

    public int getTimeout() {
        return timeout;
    }

    public void reduceTimeout() {
        this.timeout--;
    }

}
