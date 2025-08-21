package com.mercado.sistema.dao.repository;

import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.dao.model.Terminal;
import com.mercado.sistema.dao.model.Visualizacao;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisualizacaoRepository extends JpaRepository<Visualizacao, Long> {
    List<Visualizacao> findByTerminal(Terminal terminal);
    List<Visualizacao> findByProduto(Produto produto);
    List<Visualizacao> findByDataHoraBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    @Query("SELECT COUNT(v) FROM Visualizacao v WHERE v.terminal = :terminal")
    Long countByTerminal(Terminal terminal);
    @Query("SELECT COUNT(v) FROM Visualizacao v WHERE v.produto = :produto")
    Long countByProduto(Produto produto);
    void deleteByProduto(Produto produto);
    void deleteByTerminal(Terminal terminal);
}
