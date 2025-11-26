package org.example.service.equipamento;

import org.example.model.Equipamento;
import org.example.repository.EquipamentoRepository;

import java.sql.SQLException;

public class EquipamentoServiceImpl implements EquipamentoService
{

    EquipamentoRepository repo = new EquipamentoRepository();
    @Override
    public Equipamento criarEquipamento(Equipamento equipamento) throws SQLException {

        equipamento.setStatusOperacional("OPERACIONAL");
        equipamento = repo.cadastrarEquipamento(equipamento);

        if (equipamento.getId() == null) {
            throw new RuntimeException("Ocorreu um erro!");
        }

        return equipamento;
    }

    @Override
    public Equipamento buscarEquipamentoPorId(Long id) throws SQLException {

        Equipamento eq = repo.buscarEquipamento(id);

        if (eq == null) {
            throw new RuntimeException("Equipamento não encontrado!");
        }
        return eq;
    }
}
