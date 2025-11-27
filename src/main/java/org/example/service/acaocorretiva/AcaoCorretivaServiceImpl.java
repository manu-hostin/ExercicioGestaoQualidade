package org.example.service.acaocorretiva;

import org.example.model.AcaoCorretiva;
import org.example.model.Falha;
import org.example.repository.AcaoCorretivaRepository;
import org.example.repository.EquipamentoRepository;
import org.example.repository.FalhaRepository;

import java.sql.SQLException;

public class AcaoCorretivaServiceImpl implements AcaoCorretivaService{

    @Override
    public AcaoCorretiva registrarConclusaoDeAcao (AcaoCorretiva acao) throws SQLException {
        FalhaRepository falhaRepo = new FalhaRepository();
        AcaoCorretivaRepository repo = new AcaoCorretivaRepository();
        EquipamentoRepository eqRepo = new EquipamentoRepository();

        Falha falha = falhaRepo.buscarFalhaPorId(acao.getFalhaId());

        if (!falhaRepo.verificarFalha(acao.getFalhaId())){
            throw new RuntimeException("Falha não encontrada!");
        }
        repo.salvarAcao(acao);
        falhaRepo.atualizarStatus(acao.getFalhaId());

        if(falha.getCriticidade().equals("CRITICA")){
            eqRepo.atualizarStatus("OPERACIONAL", falha.getEquipamentoId());
        }

        return acao;
    }
}
