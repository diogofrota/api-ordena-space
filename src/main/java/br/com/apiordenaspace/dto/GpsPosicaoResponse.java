package br.com.apiordenaspace.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record GpsPosicaoResponse(
        String tabletSatelital,
        BigDecimal latitude,
        BigDecimal longitude,
        Instant capturadoEm,
        String observacao
) {
}
