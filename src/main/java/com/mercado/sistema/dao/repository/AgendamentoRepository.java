package com.mercado.sistema.dao.repository;

import com.mercado.sistema.dao.model.Agendamento;
import com.mercado.sistema.dao.model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
  List<Agendamento> findAllByOrderByTituloAscDataInicioDescTerminalConteudo();
  List<Agendamento> findByUsuario(Usuario usuario);
  List<Agendamento> findByDataInicioBetween(LocalDateTime inicio, LocalDateTime fim);
  @Query("SELECT a FROM Agendamento a WHERE a.dataInicio <= :fim AND a.dataFim >= :inicio")
  List<Agendamento> findConflitosAgendamento(LocalDateTime inicio, LocalDateTime fim);
}
