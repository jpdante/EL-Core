package com.ellisiumx.elcore.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RepositoryBase implements Listener {

    private DataSource _dataSource;
    private final Object _repositoryLock = new Object();

    protected JavaPlugin Plugin;

    public RepositoryBase(JavaPlugin plugin, DataSource dataSource) {
        Plugin = plugin;
        _dataSource = dataSource;

        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                initialize();
                update();
            }
        });

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected abstract void initialize();

    protected abstract void update();

    protected DataSource getConnectionPool() {
        return _dataSource;
    }

    protected Connection getConnection() {
        try {
            return _dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void executeAsyncCallbackQuery(PreparedStatement statement, DatabaseRunnable<ResultSet> runnable) {
        runnable.setSuccess(false);
        Thread asyncThread = new Thread(new Runnable() {
            public void run() {
                try {
                    ResultSet resultSet = statement.executeQuery();
                    runnable.setSuccess(true);
                    synchronized (_repositoryLock) {
                        runnable.run(resultSet);
                    }
                } catch (Exception exception) {
                    synchronized (_repositoryLock) {
                        runnable.run(null);
                    }
                }
            }
        });
    }

    protected void executeAsyncCallbackUpdate(PreparedStatement statement, DatabaseRunnable<Integer> runnable) {
        runnable.setSuccess(false);
        Thread asyncThread = new Thread(new Runnable() {
            public void run() {
                try {
                    int data = statement.executeUpdate();
                    runnable.setSuccess(true);
                    synchronized (_repositoryLock) {
                        runnable.run(data);
                    }
                } catch (Exception exception) {
                    synchronized (_repositoryLock) {
                        runnable.run(0);
                    }
                }
            }
        });
    }

    @EventHandler
    public void processDatabaseQueue(UpdateEvent event) {
        if (event.getType() != UpdateType.SEC) return;

    }
}
