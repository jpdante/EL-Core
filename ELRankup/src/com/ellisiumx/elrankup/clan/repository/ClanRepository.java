package com.ellisiumx.elrankup.clan.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import org.bukkit.plugin.java.JavaPlugin;

public class ClanRepository extends RepositoryBase {
    public ClanRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void update() {

    }
}
