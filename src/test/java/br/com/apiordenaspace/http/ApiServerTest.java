package br.com.apiordenaspace.http;

import br.com.apiordenaspace.config.AppConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiServerTest {

    private ApiServer apiServer;

    @AfterEach
    void tearDown() {
        if (apiServer != null) {
            apiServer.stop();
        }
    }

    @Test
    void deveRetornarUltimaPosicaoQuandoTabletExistir() throws Exception {
        apiServer = ApiServer.create(new AppConfig(
                0,
                "jdbc:h2:mem:teste_ok;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "sa",
                "",
                true
        ));
        apiServer.start();

        HttpResponse<String> response = enviarGet("/api/telemetria/tablets/80001/ultima-posicao");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"tabletSatelital\":\"80001\""));
        assertTrue(response.body().contains("\"observacao\":\"simulada dentro da area\""));
    }

    @Test
    void deveRetornar404QuandoTabletNaoExistir() throws Exception {
        apiServer = ApiServer.create(new AppConfig(
                0,
                "jdbc:h2:mem:teste_404;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "sa",
                "",
                true
        ));
        apiServer.start();

        HttpResponse<String> response = enviarGet("/api/telemetria/tablets/99999/ultima-posicao");

        assertEquals(404, response.statusCode());
        assertEquals("{\"error\":\"Tablet satelital nao encontrado.\"}", response.body());
    }

    private HttpResponse<String> enviarGet(String path) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + apiServer.getPort() + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
