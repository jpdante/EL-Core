package com.ellisiumx.elcore.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;

import org.apache.commons.dbcp2.BasicDataSource;

public final class DBPool {

    public static DataSource Core;

    private static DBPool context;

    private String url;
    private String username;
    private String password;
    private HashMap<String , DataSource> sources;

    public static DataSource openDataSource(String url, String username, String password) {
        BasicDataSource source = new BasicDataSource();
        source.addConnectionProperty("autoReconnect", "true");
        source.addConnectionProperty("allowMultiQueries", "true");
        source.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        source.setMaxTotal(4);
        source.setMaxIdle(4);
        source.setTimeBetweenEvictionRunsMillis(180 * 1000);
        source.setSoftMinEvictableIdleTimeMillis(180 * 1000);
        return source;
    }

    public DBPool(String url, String username, String password) {
        context = this;
        this.url = url;
        this.username = username;
        this.password = password;
        this.sources = new HashMap<>();
        Core = openDataSource(url + "/elcore", username, password);
    }

    public static void registerDataSource(String name, String database) {
        DataSource dataSource = openDataSource(context.url + "/" + database, context.username, context.password);
        context.sources.put(name, dataSource);
    }

    public static void unregisterDataSource(String name) {
        context.sources.remove(name);
    }

    public static DataSource getDataSource(String name) {
        return context.sources.get(name);
    }
}
