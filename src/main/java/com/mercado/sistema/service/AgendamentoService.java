package com.mercado.sistema.service;

import com.mercado.sistema.dao.model.*;
import com.mercado.sistema.dao.model.Agendamento;
import com.mercado.sistema.dao.repository.AgendamentoRepository;
import com.mercado.sistema.dao.repository.ConteudoRepository;
import com.mercado.sistema.dao.repository.TerminalConteudoRepository;
import com.mercado.sistema.dao.repository.TerminalRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendamentoService {
    public static final String ERRO_NA_TRANSACAO_ROLLBACK_SERÁ_EXECUTADO = "❌ Erro na transação - Rollback será executado: ";
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private ConteudoRepository conteudoRepository;
    @Autowired
    private TerminalRepository terminalRepository;
    @Autowired
    private TerminalConteudoRepository tConteudoRepository;

    public List<Agendamento> findAll() {
        return agendamentoRepository.findAllByOrderByTituloAscDataInicioDescTerminalConteudo();
    }

    public Optional<Agendamento> findById(Long id) {
        return agendamentoRepository.findById(id);
    }

    public List<Agendamento> findByUsuario(Usuario usuario) {
        return agendamentoRepository.findByUsuario(usuario);
    }

    public List<Agendamento> findByDataInicioBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return agendamentoRepository.findByDataInicioBetween(dataInicio, dataFim);
    }

    public List<Agendamento> findConflitosAgendamento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return agendamentoRepository.findConflitosAgendamento(dataInicio, dataFim);
    }

    public TerminalConteudo saveTConteudo(Long conteudoId, Long terminalId) {
        TerminalConteudo terminalConteudo = new TerminalConteudo();
        terminalConteudo.setConteudo(conteudoRepository.getReferenceById(conteudoId));
        terminalConteudo.setTerminal(terminalRepository.getReferenceById(terminalId));
        return tConteudoRepository.save(terminalConteudo);
    }

    public Agendamento save(Agendamento agendamento, Long conteudoId, Long terminalId) {
        try{
            TerminalConteudo terminalConteudo = saveTConteudo(conteudoId, terminalId);
            if(terminalConteudo==null){
                throw new Exception("Conteúdo não gerado.");
            }
            agendamento.setTerminalConteudo(terminalConteudo);
            return agendamentoRepository.save(agendamento);
        } catch (Exception e) {
            System.err.println(ERRO_NA_TRANSACAO_ROLLBACK_SERÁ_EXECUTADO + e.getMessage());
            // O @Transactional automaticamente faz rollback para qualquer Exception
            throw new RuntimeException("Erro ao atualizar BD na tabela produto: " + e.getMessage(), e);
        }
    }

    public void deleteById(long id) {
        agendamentoRepository.deleteById(id);
    }

    public boolean temConflito(Agendamento agendamento) {
        List<Agendamento> conflitosAgendamento = findConflitosAgendamento(agendamento.getDataInicio(), agendamento.getDataFim());
        /*
        return conflitosAgendamento.stream()
                .anyMatch(c -> !c.getId().equals(agendamento.getId()) &&
                        c.getTerminalConteudo().getId() == agendamento.getTerminalConteudo().getId());*/
        return false;
    }

}
