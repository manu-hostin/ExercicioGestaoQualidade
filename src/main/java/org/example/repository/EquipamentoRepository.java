package org.example.repository;

import org.example.database.Conexao;
import org.example.dto.EquipamentoContagemFalhasDTO;
import org.example.model.Equipamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoRepository {

    public Equipamento cadastrarEquipamento (Equipamento eq) throws SQLException{
        String query = "INSERT INTO Equipamento (nome, numeroDeSerie, areaSetor, statusOperacional) VALUES (?, ?, ?, ?)";

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, eq.getNome());
            stmt.setString(2, eq.getNumeroDeSerie());
            stmt.setString(3, eq.getAreaSetor());
            stmt.setString(4, eq.getStatusOperacional());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){
                eq.setId(rs.getLong(1));
            }
        }
        return eq;
    }
    public Equipamento buscarEquipamento (long id) throws SQLException{
        String query = "SELECT id, nome, numeroDeSerie, areaSetor, statusOperacional FROM Equipamento WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String numero = rs.getString("numeroDeSerie");
                String area = rs.getString("areaSetor");
                String status = rs.getString("statusOperacional");

                return new Equipamento(id, nome, numero, area, status);
            }
        }
        return null;
    }
    public boolean verificarExistencia (long id) throws SQLException{
        String query = "SELECT COUNT(0) FROM Equipamento WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) > 0;
            }
        }
        return false;
    }
    public void atualizarStatus (String status, Long id) throws SQLException {
        String query = " UPDATE Equipamento set statusOperacional = ? WHERE id  = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setLong(2, id);
            stmt.executeUpdate();

        }
    }
    public List<Equipamento> buscarEquipamentosSemFalhasPorPeriodo (LocalDate dataInicio, LocalDate datafim) throws SQLException {
        String query = """
                    SELECT id
                          ,nome
                          ,numeroDeSerie
                          ,areaSetor
                          ,statusOperacional
                           FROM Equipamento
                           WHERE id NOT IN (SELECT equipamentoId
                                            FROM Falha
                                            WHERE dataHoraOcorrencia >= ?
                                            AND dataHoraOcorrencia <= ?)
                """;
        List<Equipamento> equipamentos = new ArrayList<>();
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(datafim));

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                equipamentos.add(
                        new Equipamento(
                                rs.getLong("id"),
                                rs.getString("nome"),
                                rs.getString("numeroDeSerie"),
                                rs.getString("areaSetor"),
                                rs.getString("statusOperacional")
                        )
                );
            }
        }
        return equipamentos;
    }
    public List<EquipamentoContagemFalhasDTO> gerarRelatorioManutencaoPreventiva(int contagemMinimaFalhas) throws SQLException{
        List<EquipamentoContagemFalhasDTO> resultado = new ArrayList<>();
        String sql = """
                    SELECT
                        e.id,
                        e.nome,
                        COUNT(f.id) AS totalFalhas
                    FROM Equipamento e
                    LEFT JOIN Falha f ON f.equipamentoId = e.id
                    GROUP BY e.id, e.nome
                    HAVING COUNT(f.id) >= ?
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contagemMinimaFalhas);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EquipamentoContagemFalhasDTO dto =
                        new EquipamentoContagemFalhasDTO(
                                rs.getLong("id"),
                                rs.getString("nome"),
                                rs.getInt("totalFalhas")
                        );
                resultado.add(dto);
            }
        }
        return resultado;
    }
}
