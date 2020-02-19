package com.ellisiumx.elrankup.drop;

public class PlayerDrops {

    private int accountId;
    private long drops;

    public PlayerDrops(int accountId, long drops) {
        this.accountId = accountId;
        this.drops = drops;
    }

    public int getAccountId() { return accountId; }

    public void setAccountId(int accountId) { this.accountId = accountId; }

    public long getDrops() { return drops; }

    public void setDrops(long drops) { this.drops = drops; }

    public void addDrops(long drops) {
        this.drops += drops;
    }
}
