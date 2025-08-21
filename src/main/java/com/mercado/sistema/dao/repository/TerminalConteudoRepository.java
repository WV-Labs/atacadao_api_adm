package com.mercado.sistema.dao.repository;

import com.mercado.sistema.dao.model.TerminalConteudo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalConteudoRepository extends JpaRepository<TerminalConteudo, Long> {
  
}
