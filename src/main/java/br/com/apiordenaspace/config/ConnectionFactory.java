package br.com.apiordenaspace.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private final AppConfig appConfig;

    public ConnectionFactory(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                appConfig.getDbUrl(),
                appConfig.getDbUsername(),
                appConfig.getDbPassword()
        );
    }
}
