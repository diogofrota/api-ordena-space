package br.com.apiordenaspace.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class AppConfig {

    private static final String DEFAULT_H2_URL = "jdbc:h2:file:./data/ordena_space_db;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE";
    private static final String DEFAULT_H2_USERNAME = "sa";
    private static final String DEFAULT_H2_PASSWORD = "";

    private final int port;
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final boolean initializeDatabase;

    public AppConfig(int port, String dbUrl, String dbUsername, String dbPassword, boolean initializeDatabase) {
        this.port = port;
        this.dbUrl = Objects.requireNonNull(dbUrl);
        this.dbUsername = Objects.requireNonNull(dbUsername);
        this.dbPassword = dbPassword == null ? "" : dbPassword;
        this.initializeDatabase = initializeDatabase;
    }

    public static AppConfig fromEnvironment() {
        Map<String, String> env = System.getenv();
        Map<String, String> localEnv = loadLocalEnv();
        String h2Profile = firstNonBlank(
                localEnv.get("APP_PROFILE"),
                env.get("APP_PROFILE"),
                env.get("SPRING_PROFILES_ACTIVE")
        );
        String oracleDbUrl = firstNonBlank(
                localEnv.get("ORACLE_DB_URL"),
                env.get("ORACLE_DB_URL")
        );
        String profile = firstNonBlank(
                System.getProperty("app.profile"),
                localEnv.get("APP_PROFILE"),
                env.get("APP_PROFILE"),
                env.get("SPRING_PROFILES_ACTIVE")
        );

        boolean oracleProfile = "oracle".equalsIgnoreCase(profile) || oracleDbUrl != null;
        boolean explicitH2Profile = "h2".equalsIgnoreCase(profile) || "h2".equalsIgnoreCase(h2Profile);
        String dbUrl = firstNonBlank(
                System.getProperty("app.db.url"),
                localEnv.get("APP_DB_URL"),
                env.get("APP_DB_URL"),
                oracleProfile ? oracleDbUrl : null,
                explicitH2Profile ? DEFAULT_H2_URL : null
        );
        if (dbUrl == null) {
            throw new IllegalStateException(
                    "Nenhuma configuracao de banco encontrada. Defina ORACLE_DB_URL/ORACLE_DB_USERNAME/ORACLE_DB_PASSWORD " +
                            "ou use APP_PROFILE=h2 para desenvolvimento local."
            );
        }
        String dbUsername = firstNonBlank(
                System.getProperty("app.db.username"),
                localEnv.get("APP_DB_USERNAME"),
                env.get("APP_DB_USERNAME"),
                oracleProfile ? localEnv.get("ORACLE_DB_USERNAME") : null,
                oracleProfile ? env.get("ORACLE_DB_USERNAME") : null,
                dbUrl.startsWith("jdbc:h2:") ? DEFAULT_H2_USERNAME : null
        );
        String dbPassword = firstNonBlank(
                System.getProperty("app.db.password"),
                localEnv.get("APP_DB_PASSWORD"),
                env.get("APP_DB_PASSWORD"),
                oracleProfile ? localEnv.get("ORACLE_DB_PASSWORD") : null,
                oracleProfile ? env.get("ORACLE_DB_PASSWORD") : null,
                dbUrl.startsWith("jdbc:h2:") ? DEFAULT_H2_PASSWORD : null
        );
        String portValue = firstNonBlank(
                System.getProperty("app.port"),
                localEnv.get("APP_PORT"),
                env.get("APP_PORT"),
                localEnv.get("PORT"),
                env.get("PORT"),
                "8080"
        );
        int port = Integer.parseInt(portValue);
        boolean initializeDatabase = dbUrl.startsWith("jdbc:h2:");
        return new AppConfig(port, dbUrl, dbUsername, dbPassword, initializeDatabase);
    }

    public boolean isOracleDatabase() {
        return dbUrl.startsWith("jdbc:oracle:");
    }

    public int getPort() {
        return port;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public boolean isInitializeDatabase() {
        return initializeDatabase;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static Map<String, String> loadLocalEnv() {
        Path path = Path.of(".env.local");
        if (!Files.exists(path)) {
            return Map.of();
        }

        Map<String, String> values = new HashMap<>();
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, separatorIndex).trim();
                String value = trimmed.substring(separatorIndex + 1).trim();
                values.put(key, value);
            }
            return values;
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler o arquivo .env.local.", e);
        }
    }
}
