package br.com.apiordenaspace;

import br.com.apiordenaspace.config.AppConfig;
import br.com.apiordenaspace.http.ApiServer;

public class ApiOrdenaSpaceApplication {

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.fromEnvironment();
        ApiServer server = ApiServer.create(config);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        System.out.println("API ORDENA SPACE rodando em http://localhost:" + server.getPort());
        System.out.println("Banco ativo: " + (config.isOracleDatabase() ? "ORACLE" : "H2") + " | URL: " + config.getDbUrl());
    }
}
