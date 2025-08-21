package com.mercado.sistema.service;

import com.mercado.sistema.dao.model.*;
import com.mercado.sistema.dao.model.Visualizacao;
import com.mercado.sistema.dao.repository.VisualizacaoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisualizacaoService {
    @Autowired
    private VisualizacaoRepository visualizacaoRepository;

    public List<Visualizacao> findAll() {
        return visualizacaoRepository.findAll();
    }

    public Optional<Visualizacao> fundById(Long id) {
        return visualizacaoRepository.findById(id);
    }

    public List<Visualizacao> findByTerminal(Terminal terminal) {
        return visualizacaoRepository.findByTerminal(terminal);
    }

    public List<Visualizacao> findByProduto(Produto produto) {
        return visualizacaoRepository.findByProduto(produto);
    }

    public List<Visualizacao> findByDataHoraBetween(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        return visualizacaoRepository.findByDataHoraBetween(dataInicial, dataFinal);
    }

    public Long countByTerminal(Terminal terminal)  {
        return visualizacaoRepository.countByTerminal(terminal);
    }

    public Long countByProduto(Produto produto) {
        return visualizacaoRepository.countByProduto(produto);
    }

    public void registrarVisualizacao(Terminal terminal, Produto produto, String ip) {
        Visualizacao visualizacao = new Visualizacao(terminal, produto, ip);
        save(visualizacao);
    }

    public Visualizacao save(Visualizacao Visualizacao) {
        return visualizacaoRepository.save(Visualizacao);
    }

    public void deleteByProduto(Produto produto){
        visualizacaoRepository.deleteByProduto(produto);
    }
}
