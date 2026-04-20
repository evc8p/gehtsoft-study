package com.evch.rrm.webserver.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class Repository {
    private static final Properties properties = new Properties();
    private static final HikariConfig hikariConfig = new HikariConfig();
    private static HikariDataSource hikariDataSource;

    static {
        try (InputStream in = Repository.class.getClassLoader().getResourceAsStream("db.properties")) {
            properties.load(in);
            Class.forName(properties.getProperty("db.driver"));
            hikariConfig.setJdbcUrl(properties.getProperty("db.url"));
            hikariConfig.setUsername(properties.getProperty("db.username"));
            hikariConfig.setPassword(properties.getProperty("db.password"));
            hikariConfig.addDataSourceProperty("allowPublicKeyRetrieval", "true");
            hikariConfig.addDataSourceProperty("useSSL", "false");

            hikariConfig.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.maxConnections")));
            hikariConfig.setMinimumIdle(Integer.parseInt(properties.getProperty("db.minimumIdle")));
            hikariConfig.setConnectionTimeout(Integer.parseInt(properties.getProperty("db.connectionTimeout")));
            hikariConfig.setIdleTimeout(Integer.parseInt(properties.getProperty("db.idleTimeout")));
            hikariConfig.setMaxLifetime(Integer.parseInt(properties.getProperty("db.maxLifetime")));

            hikariDataSource = new HikariDataSource(hikariConfig);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public static void closeConnection() {
        if (Objects.nonNull(hikariDataSource)) {
            hikariDataSource.close();
        }
    }
}
