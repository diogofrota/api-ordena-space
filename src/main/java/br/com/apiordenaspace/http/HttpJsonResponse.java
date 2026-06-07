package br.com.apiordenaspace.http;

import br.com.apiordenaspace.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class HttpJsonResponse {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private HttpJsonResponse() {
    }

    public static void ok(HttpExchange exchange, Object body) throws IOException {
        write(exchange, 200, body);
    }

    public static void badRequest(HttpExchange exchange, String message) throws IOException {
        write(exchange, 400, new ErrorResponse(message));
    }

    public static void notFound(HttpExchange exchange, String message) throws IOException {
        write(exchange, 404, new ErrorResponse(message));
    }

    public static void methodNotAllowed(HttpExchange exchange) throws IOException {
        write(exchange, 405, new ErrorResponse("Metodo nao permitido."));
    }

    public static void internalServerError(HttpExchange exchange) throws IOException {
        write(exchange, 500, new ErrorResponse("Erro interno do servidor."));
    }

    private static void write(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
