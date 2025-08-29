package com.mercado.sistema.dao.repository;

import com.mercado.sistema.dao.model.Conteudo;
import com.mercado.sistema.dao.model.ConteudoTabelaPreco;
import com.mercado.sistema.dao.model.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConteudoTabelaPrecoRepository extends JpaRepository<ConteudoTabelaPreco, Long> {
  String SQLCOMUM = " FROM Produto p  " +
          " INNER JOIN ConteudoTabelaPreco ctp ON p.id = ctp.produto.id and p.ativo=true ";
  String SQLTERMINAL = " INNER JOIN TerminalConteudo t ON ctp.conteudo.id = t.conteudo.id ";
  @Query("SELECT distinct p " + SQLCOMUM + SQLTERMINAL + " and t.terminal.id = :terminalId ")
  List<Produto> findProdutos(Long terminalId);
  @Query("SELECT distinct p " + SQLCOMUM + " and ctp.conteudo.id = :conteudoId " + SQLTERMINAL)
  List<Produto> findProdutosConteudo(Long conteudoId);
  List<ConteudoTabelaPreco> findByConteudo(Conteudo conteudo);
}
