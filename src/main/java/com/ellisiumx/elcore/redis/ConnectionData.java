package com.ellisiumx.elcore.redis;

public class ConnectionData {

    public enum ConnectionType {
        MASTER,
        SLAVE;
    }

    private ConnectionType _type;

    public ConnectionType getType() {
        return _type;
    }

    private String _name;

    public String getName() {
        return _name;
    }

    private String _host;

    public String getHost() {
        return _host;
    }

    private int _port;

    public int getPort() {
        return _port;
    }

    public ConnectionData(String host, int port, ConnectionType type, String name) {
        _host = host;
        _port = port;
        _type = type;
        _name = name;
    }

    public boolean nameMatches(String name) {
        return (name == null || name.equalsIgnoreCase(_name));
    }
}