package com.ellisiumx.elrankup.cash.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import org.bukkit.plugin.java.JavaPlugin;

public class CashRepository extends RepositoryBase {
    public CashRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void update() {

    }
}
