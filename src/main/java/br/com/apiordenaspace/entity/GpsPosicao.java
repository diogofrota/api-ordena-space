package br.com.apiordenaspace.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class GpsPosicao {

    private Long id;
    private String tabletSatelital;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Instant capturadoEm;
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTabletSatelital() {
        return tabletSatelital;
    }

    public void setTabletSatelital(String tabletSatelital) {
        this.tabletSatelital = tabletSatelital;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Instant getCapturadoEm() {
        return capturadoEm;
    }

    public void setCapturadoEm(Instant capturadoEm) {
        this.capturadoEm = capturadoEm;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
