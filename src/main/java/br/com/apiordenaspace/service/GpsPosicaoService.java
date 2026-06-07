package br.com.apiordenaspace.service;

import br.com.apiordenaspace.dto.GpsPosicaoResponse;
import br.com.apiordenaspace.entity.GpsPosicao;
import br.com.apiordenaspace.exception.BadRequestException;
import br.com.apiordenaspace.exception.TabletSatelitalNotFoundException;
import br.com.apiordenaspace.repository.GpsPosicaoRepository;

public class GpsPosicaoService {

    private final GpsPosicaoRepository gpsPosicaoRepository;

    public GpsPosicaoService(GpsPosicaoRepository gpsPosicaoRepository) {
        this.gpsPosicaoRepository = gpsPosicaoRepository;
    }

    public GpsPosicaoResponse buscarUltimaPosicao(String tabletSatelital) {
        String tabletNormalizado = normalizarTabletSatelital(tabletSatelital);
        GpsPosicao gpsPosicao = gpsPosicaoRepository.findByTabletSatelital(tabletNormalizado)
                .orElseThrow(TabletSatelitalNotFoundException::new);

        return new GpsPosicaoResponse(
                gpsPosicao.getTabletSatelital(),
                gpsPosicao.getLatitude(),
                gpsPosicao.getLongitude(),
                gpsPosicao.getCapturadoEm(),
                gpsPosicao.getObservacao()
        );
    }

    private String normalizarTabletSatelital(String tabletSatelital) {
        if (tabletSatelital == null || tabletSatelital.trim().isEmpty()) {
            throw new BadRequestException("tabletSatelital nao pode ser vazio.");
        }
        return tabletSatelital.trim();
    }
}
