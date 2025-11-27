package org.example.repository;

import org.example.database.Conexao;
import org.example.model.AcaoCorretiva;

import java.sql.*;

public class AcaoCorretivaRepository {

    public AcaoCorretiva salvarAcao (AcaoCorretiva acao) throws SQLException {
        String query = "INSERT INTO AcaoCorretiva (falhaId, dataHoraInicio, dataHoraFim, responsavel, descricaoAcao) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            stmt.setLong(1, acao.getFalhaId());
            stmt.setTimestamp(2, Timestamp.valueOf(acao.getDataHoraInicio()));
            stmt.setTimestamp(3, Timestamp.valueOf(acao.getDataHoraFim()));
            stmt.setString(4, acao.getResponsavel());
            stmt.setString(5, acao.getDescricaoArea());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                acao.setId(rs.getLong(1));
            }
        }
        return acao;
    }
}
