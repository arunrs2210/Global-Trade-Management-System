package com.shnoor.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection — singleton connection manager for PostgreSQL.
 * Loads credentials from db.properties on the classpath.
 */
public class DBConnection {

    private static DBConnection instance;
    private static final Properties props = new Properties();

    static {
        try (InputStream in = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found on classpath");
            props.load(in);
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {}

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) instance = new DBConnection();
            }
        }
        return instance;
    }

    /**
     * Opens and returns a new JDBC Connection.
     * Caller is responsible for closing it (use try-with-resources).
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
        );
    }
}
