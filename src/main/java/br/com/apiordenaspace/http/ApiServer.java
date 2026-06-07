package br.com.apiordenaspace.http;

import br.com.apiordenaspace.config.AppConfig;
import br.com.apiordenaspace.config.ConnectionFactory;
import br.com.apiordenaspace.config.DatabaseInitializer;
import br.com.apiordenaspace.controller.OpenApiHandler;
import br.com.apiordenaspace.controller.SwaggerUiHandler;
import br.com.apiordenaspace.controller.TelemetriaHandler;
import br.com.apiordenaspace.exception.BadRequestException;
import br.com.apiordenaspace.exception.TabletSatelitalNotFoundException;
import br.com.apiordenaspace.repository.GpsPosicaoRepository;
import br.com.apiordenaspace.service.GpsPosicaoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiServer {

    private final HttpServer httpServer;
    private final ExecutorService executorService;

    private ApiServer(HttpServer httpServer, ExecutorService executorService) {
        this.httpServer = httpServer;
        this.executorService = executorService;
    }

    public static ApiServer create(AppConfig config) throws IOException {
        ConnectionFactory connectionFactory = new ConnectionFactory(config);
        if (config.isInitializeDatabase()) {
            new DatabaseInitializer(connectionFactory).initialize();
        }

        GpsPosicaoRepository repository = new GpsPosicaoRepository(connectionFactory);
        GpsPosicaoService service = new GpsPosicaoService(repository);

        HttpServer server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
        ExecutorService executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);

        server.createContext("/api/telemetria/tablets", exchange -> handleSafely(exchange, new TelemetriaHandler(service)));
        server.createContext("/openapi.json", exchange -> handleSafely(exchange, new OpenApiHandler()));
        server.createContext("/swagger-ui.html", exchange -> handleSafely(exchange, new SwaggerUiHandler()));
        server.createContext("/", exchange -> handleRoot(exchange));
        return new ApiServer(server, executor);
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        executorService.shutdownNow();
    }

    public int getPort() {
        return httpServer.getAddress().getPort();
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        if (!"/".equals(exchange.getRequestURI().getPath())) {
            HttpJsonResponse.notFound(exchange, "Endpoint nao encontrado.");
            return;
        }
        byte[] body = "API ORDENA SPACE em execucao".getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static void handleSafely(HttpExchange exchange, com.sun.net.httpserver.HttpHandler delegate) throws IOException {
        try {
            delegate.handle(exchange);
        } catch (BadRequestException ex) {
            HttpJsonResponse.badRequest(exchange, ex.getMessage());
        } catch (TabletSatelitalNotFoundException ex) {
            HttpJsonResponse.notFound(exchange, ex.getMessage());
        } catch (Exception ex) {
            HttpJsonResponse.internalServerError(exchange);
        }
    }
}
