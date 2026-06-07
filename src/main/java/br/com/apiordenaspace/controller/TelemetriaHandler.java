package br.com.apiordenaspace.controller;

import br.com.apiordenaspace.dto.GpsPosicaoResponse;
import br.com.apiordenaspace.exception.BadRequestException;
import br.com.apiordenaspace.service.GpsPosicaoService;
import br.com.apiordenaspace.http.HttpJsonResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class TelemetriaHandler implements HttpHandler {

    private static final String PREFIX = "/api/telemetria/tablets/";
    private static final String SUFFIX = "/ultima-posicao";

    private final GpsPosicaoService gpsPosicaoService;

    public TelemetriaHandler(GpsPosicaoService gpsPosicaoService) {
        this.gpsPosicaoService = gpsPosicaoService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpJsonResponse.methodNotAllowed(exchange);
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        if (!requestPath.startsWith(PREFIX) || !requestPath.endsWith(SUFFIX)) {
            HttpJsonResponse.notFound(exchange, "Endpoint nao encontrado.");
            return;
        }

        String tabletEncoded = requestPath.substring(PREFIX.length(), requestPath.length() - SUFFIX.length());
        String tabletSatelital = URLDecoder.decode(tabletEncoded, StandardCharsets.UTF_8);
        if (tabletSatelital.endsWith("/")) {
            tabletSatelital = tabletSatelital.substring(0, tabletSatelital.length() - 1);
        }
        if (tabletSatelital.contains("/")) {
            throw new BadRequestException("tabletSatelital nao pode ser vazio.");
        }

        GpsPosicaoResponse response = gpsPosicaoService.buscarUltimaPosicao(tabletSatelital);
        HttpJsonResponse.ok(exchange, response);
    }
}
