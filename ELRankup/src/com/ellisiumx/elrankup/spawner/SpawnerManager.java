package com.ellisiumx.elrankup.spawner;

import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elrankup.spawner.command.SpawnerCommand;
import com.ellisiumx.elrankup.spawner.repository.SpawnerRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerManager implements Listener {

    public static SpawnerManager context;
    private SpawnerRepository repository;

    public SpawnerManager(JavaPlugin plugin) {
        context = this;
        repository = new SpawnerRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            // Errors
            languageDB.insertTranslation("CannotRankUPMax", "&f[&aRankup&f] &cYou can no longer level up because you have already reached the maximum level.");
            languageDB.insertTranslation("CannotRankUPNoMoney", "&f[&aRankup&f] &cYou don't have enough money to level up(%Cost%).");
            languageDB.insertTranslation("CannotRankUPFailed", "&f[&aRankup&f] &cThere was a problem with the server, please try again later %Error%.");
            // Messages
            languageDB.insertTranslation("RankUPSuccess", "&f[&aRankup&f] &aYou have successfully leveled up!");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new SpawnerCommand(plugin);
    }
}
