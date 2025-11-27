package org.example.repository;

import org.example.database.Conexao;
import org.example.dto.FalhaDetalhadaDTO;
import org.example.dto.RelatorioParadaDTO;
import org.example.model.Falha;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FalhaRepository {

    public Falha registrarNovaFalha (Falha falha) throws SQLException{
        String query = "INSERT INTO Falha (equipamentoId, dataHoraOcorrencia, descricao, criticidade, status, tempoParadaHoras) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, falha.getEquipamentoId());
            stmt.setTimestamp(2, Timestamp.valueOf(falha.getDataHoraOcorrencia()));
            stmt.setString(3, falha.getDescricao());
            stmt.setString(4, falha.getCriticidade());
            stmt.setString(5, falha.getStatus());
            stmt.setBigDecimal(6,falha.getTempoParadaHoras());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){
                falha.setId(rs.getLong(1));
            }
        }
        return falha;
    }
    public List<Falha> buscarFalhasAbertas () throws SQLException {
        String query = "SELECT id, equipamentoId, dataHoraOcorrencia, descricao, criticidade, status, tempoParadaHoras FROM Falha WHERE criticidade = 'CRITICA' AND status = 'ABERTA'";

        List<Falha> falhasAbertas = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)){

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Falha falha = new Falha();
                falha.setId(rs.getLong("id"));
                falha.setEquipamentoId(rs.getLong("equipamentoId"));
                falha.setDataHoraOcorrencia(rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime());
                falha.setDescricao(rs.getString("descricao"));
                falha.setCriticidade(rs.getString("criticidade"));
                falha.setStatus(rs.getString("status"));
                falha.setTempoParadaHoras(rs.getBigDecimal("tempoParadaHoras"));

                falhasAbertas.add(falha);
            }
        }
        return falhasAbertas;
    }
    public boolean verificarFalha (Long id) throws SQLException {
        String query = "SELECT COUNT(0) FROM Falha WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) > 0;
            }
        }
        return false;
    }
    public void atualizarStatus (Long id) throws SQLException {
        String query = """
                UPDATE Falha
                SET status = 'RESOLVIDA'
                WHERE id = ?
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)) {

             stmt.setLong(1, id);
             stmt.executeUpdate();
        }
    }
    public Falha buscarFalhaPorId(Long id) throws SQLException{
        String query = """
                SELECT  id
                        ,equipamentoId
                        ,dataHoraOcorrencia
                        ,descricao
                        ,criticidade
                        ,status
                        ,tempoParadaHoras
                FROM Falha
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1,id);
            ResultSet rs = stmt.executeQuery();


            while(rs.next()){
                Long equipamentoId = rs.getLong("equipamentoId");
                LocalDateTime dataHoraOcorrencia = rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime();
                String descricao = rs.getString("descricao");
                String criticidade = rs.getString("criticidade");
                String status = rs.getString("status");
                BigDecimal tempoParadaHoras = rs.getBigDecimal("tempoParadaHoras");

                return new Falha(
                        id,
                        equipamentoId,
                        dataHoraOcorrencia,
                        descricao,
                        criticidade,
                        status,
                        tempoParadaHoras
                );
            }
        }
        return null;
    }
    public List<RelatorioParadaDTO> gerarRelatorioTempoParada () throws SQLException {
        String query = """
                SELECT e.id,
                       e.nome,
                       f.tempoParadaHoras
                FROM Equipamento e
                JOIN Falha f ON f.equipamentoID = e.id
                """;
        List<RelatorioParadaDTO> relatorio = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String nome = rs.getString("nome");
                Double tempo = rs.getDouble("tempoParadaHoras");

                RelatorioParadaDTO rel = new RelatorioParadaDTO(id, nome, tempo);
                relatorio.add(rel);
            }
        }
        return relatorio;
    }
    public Optional<FalhaDetalhadaDTO> buscarDetalhesCompletosFalha(long falhaId) throws SQLException {

    }


}
