package com.ellisiumx.elcore.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetCallable {
    public void processResultSet(ResultSet resultSet) throws SQLException;
}
