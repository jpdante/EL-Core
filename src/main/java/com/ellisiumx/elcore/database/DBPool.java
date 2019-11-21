package com.ellisiumx.elcore.database;

import javax.sql.DataSource;
import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;

public final class DBPool {

    public static DataSource Default;

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
        Default = openDataSource(url, username, password);
    }
}
