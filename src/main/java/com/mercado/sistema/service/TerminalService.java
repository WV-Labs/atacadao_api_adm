package com.mercado.sistema.service;

import com.mercado.sistema.dao.model.Categoria;
import com.mercado.sistema.dao.model.Terminal;
import com.mercado.sistema.dao.repository.ConteudoTabelaPrecoRepository;
import com.mercado.sistema.dao.repository.TerminalRepository;
import com.mercado.sistema.dao.repository.VisualizacaoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerminalService {
    @Autowired
    private TerminalRepository terminalRepository;
    @Autowired
    private VisualizacaoRepository visualizacaoRepository;
    @Autowired
    private ConteudoTabelaPrecoRepository conteudoTabelaPrecoRepository;

    public List<Terminal> findAll() {
        return terminalRepository.findAllByAtivoTrueOrderByNomeAscLocalizacaoAsc();
    }

    public Optional<Terminal> findById(Long id) {
        return terminalRepository.findById(id);
    }

    public List<Terminal> findByCategoria(Categoria categoria) {
        return terminalRepository.findByCategoria(categoria);
    }

    public List<Terminal> findByAtivo(Boolean ativo) {
        return terminalRepository.findByAtivo(ativo);
    }

    public Optional<Terminal> findByCategoriaAndNumero(Categoria categoria, Integer numero) {
        return terminalRepository.findByCategoriaAndNrTerminal(categoria, numero);
    }

    public Optional<Terminal> findByCategoriaNomeAndNumero(String nomeCategoria, Integer numero)  {
        return terminalRepository.findByNomeCategoriaAssociadoAndNrTerminal(nomeCategoria, numero);
    }

    public Terminal save(Terminal terminal) {
        Terminal save = terminalRepository.save(terminal);
        return save;
    }

    public void deleteById(long id) {
        Optional<Terminal> terminal = findById(id);
        visualizacaoRepository.deleteByTerminal(terminal.get());
        terminalRepository.deleteById(id);
    }
}
