package com.ellisiumx.elrankup.machine.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import org.bukkit.plugin.java.JavaPlugin;

public class MachineRepository extends RepositoryBase {

    public MachineRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

}
