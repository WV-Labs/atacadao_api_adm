package com.mercado.sistema.dao.repository;

import com.mercado.sistema.dao.model.Conteudo;
import com.mercado.sistema.dao.model.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {
  String SQLCOMUM= " from Produto p " +
          " LEFT JOIN ConteudoTabelaPreco ctp on p.id  = ctp.produto.id and p.ativo=true " +
          " INNER JOIN Conteudo c2 on ctp.conteudo.id = c2.id " +
          " INNER JOIN TerminalConteudo t on c2.id = t.id and t.terminal.id = :terminalId ";
  @Query("SELECT p " + SQLCOMUM)
  List<Produto> findProdutos(Long terminalId);
  @Query("SELECT p.id "+ SQLCOMUM)
  List<Long> findProdutosId(Long terminalId);
  Conteudo findConteudoById(Long id);
}
