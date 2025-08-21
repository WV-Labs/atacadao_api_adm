package com.mercado.sistema.dao.repository;

import com.mercado.sistema.dao.model.Categoria;
import com.mercado.sistema.dao.model.Terminal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {
  List<Terminal> findAllByAtivoTrueOrderByNomeAscLocalizacaoAsc();
  List<Terminal> findByCategoria(Categoria categoria);
  List<Terminal> findByAtivo(Boolean ativo);
  Optional<Terminal> findByCategoriaAndNrTerminal(Categoria categoria, Integer nrTerminal);
  @Query(
      "SELECT t FROM Terminal t WHERE t.categoria.id in (select c.id from Categoria c where UPPER(c.nome) = UPPER( ?1 ))"
          + "and t.nrTerminal = ?2")
  Optional<Terminal> findByNomeCategoriaAssociadoAndNrTerminal(
      String categoriaNome, Integer nrTerminal);
}
