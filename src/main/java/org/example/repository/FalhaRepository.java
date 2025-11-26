package org.example.repository;

import org.example.database.Conexao;
import org.example.model.Falha;

import java.sql.*;

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

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                falha.setId(rs.getLong(1));
            }
        }
        return falha;
    }

}
