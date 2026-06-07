package br.com.apiordenaspace.repository;

import br.com.apiordenaspace.config.ConnectionFactory;
import br.com.apiordenaspace.entity.GpsPosicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class GpsPosicaoRepository {

    private static final String SQL = """
            SELECT id, tablet_satelital, latitude, longitude, capturado_em, observacao
            FROM gps_posicoes
            WHERE tablet_satelital = ?
            """;

    private final ConnectionFactory connectionFactory;

    public GpsPosicaoRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Optional<GpsPosicao> findByTabletSatelital(String tabletSatelital) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, tabletSatelital);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                GpsPosicao gpsPosicao = new GpsPosicao();
                gpsPosicao.setId(resultSet.getLong("id"));
                gpsPosicao.setTabletSatelital(resultSet.getString("tablet_satelital"));
                gpsPosicao.setLatitude(resultSet.getBigDecimal("latitude"));
                gpsPosicao.setLongitude(resultSet.getBigDecimal("longitude"));
                gpsPosicao.setCapturadoEm(readInstant(resultSet));
                gpsPosicao.setObservacao(resultSet.getString("observacao"));
                return Optional.of(gpsPosicao);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao consultar a tabela gps_posicoes.", e);
        }
    }

    private java.time.Instant readInstant(ResultSet resultSet) throws SQLException {
        try {
            LocalDateTime localDateTime = resultSet.getObject("capturado_em", LocalDateTime.class);
            return localDateTime.toInstant(ZoneOffset.UTC);
        } catch (SQLException ex) {
            Timestamp timestamp = resultSet.getTimestamp("capturado_em");
            return timestamp.toInstant();
        }
    }
}
