package org.example.service.falha;

import org.example.model.Falha;
import org.example.repository.EquipamentoRepository;
import org.example.repository.FalhaRepository;

import java.sql.SQLException;
import java.util.List;

public class FalhaServiceImpl implements FalhaService{

    FalhaRepository repo = new FalhaRepository();

    @Override
    public Falha registrarNovaFalha(Falha falha) throws SQLException {
        EquipamentoRepository eqRepo = new EquipamentoRepository();

        if(eqRepo.verificarExistencia(falha.getEquipamentoId())){
            throw new IllegalArgumentException("Equipamento não encontrado!");
        }

        falha.setStatus("ABERTA");
        falha = repo.registrarNovaFalha(falha);

        if (falha.getCriticidade().equals("CRITICA")) {
            eqRepo.atualizarStatus("EM_MANUTENCAO", falha.getEquipamentoId());
        }

        return falha;
    }

    @Override
    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {
        return List.of();
    }
}
