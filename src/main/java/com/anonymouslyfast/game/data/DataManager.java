package com.anonymouslyfast.game.data;

import com.anonymouslyfast.hooks.SQLiteHook;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DataManager {

    private final HashMap<String, String> tables = new HashMap<>();

    private final SQLiteHook hook;

    public DataManager(String dbPath) {
        hook = new SQLiteHook(dbPath);
    }

    private void loadTables() {
        tables.forEach((tableName, tableQuery) -> {
            try {
                Statement statement = hook.getConnection().createStatement();
                statement.executeUpdate(tableQuery);
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void registerTable(String tableName, String tableQuery) {
        tables.put(tableName, tableQuery);
    }

    public void unregisterTable(String tableName) {
        tables.remove(tableName);
    }

    public Connection getConnection() {
        return hook.getConnection();
    }
}
