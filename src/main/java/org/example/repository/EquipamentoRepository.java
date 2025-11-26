package org.example.repository;

import org.example.database.Conexao;
import org.example.model.Equipamento;

import java.sql.*;

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
                return true;
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

}
