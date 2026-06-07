package br.com.apiordenaspace.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private final ConnectionFactory connectionFactory;

    public DatabaseInitializer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void initialize() {
        executeSqlScript("schema.sql");
        executeSqlScript("data.sql");
    }

    private void executeSqlScript(String resourceName) {
        try (Connection connection = connectionFactory.getConnection();
             BufferedReader reader = new BufferedReader(new InputStreamReader(openResource(resourceName), StandardCharsets.UTF_8))) {
            connection.setAutoCommit(false);
            StringBuilder statementBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                statementBuilder.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    String sql = statementBuilder.toString().trim();
                    sql = sql.substring(0, sql.length() - 1);
                    executeStatement(connection, sql);
                    statementBuilder.setLength(0);
                }
            }
            if (statementBuilder.length() > 0) {
                executeStatement(connection, statementBuilder.toString().trim());
            }
            connection.commit();
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Falha ao inicializar o banco de dados.", e);
        }
    }

    private void executeStatement(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private InputStream openResource(String resourceName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new IllegalStateException("Recurso SQL nao encontrado: " + resourceName);
        }
        return inputStream;
    }
}
