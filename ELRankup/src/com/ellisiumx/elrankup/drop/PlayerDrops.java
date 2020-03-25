package com.ellisiumx.elrankup.drop;

public class PlayerDrops {

    private int accountId;
    private long block_drops;

    public PlayerDrops(int accountId, long blockDrops) {
        this.accountId = accountId;
        this.block_drops = blockDrops;
    }

    public int getAccountId() { return accountId; }

    public void setAccountId(int accountId) { this.accountId = accountId; }

    public long getBlockDrops() { return block_drops; }

    public void setBlockDrops(long drops) { this.block_drops = drops; }

    public void addBlockDrops(long drops) {
        this.block_drops += drops;
    }
}
