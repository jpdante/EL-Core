package com.ellisiumx.elcore.database;

import com.ellisiumx.elcore.database.column.Column;

import java.util.HashMap;

public class Row {
    public HashMap<String, Column<?>> Columns = new HashMap<String, Column<?>>();
}
