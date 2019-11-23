package com.ellisiumx.elcore.account;

import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.redis.Data;

public class ClientCache implements Data {

    public int accountId = -1;
    public String uuid = null;
    public String name = null;
    public Rank rank = Rank.ALL;

    public ClientCache(CoreClient client, String uuid) {
        this.accountId = client.getAccountId();
        this.name = client.getPlayerName();
        this.uuid = uuid;
        this.rank = client.getRank();
    }

    public ClientCache(int accountId, String uuid, String name, Rank rank) {
        this.accountId = accountId;
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
    }

    @Override
    public String getDataId() {
        return uuid;
    }
}
