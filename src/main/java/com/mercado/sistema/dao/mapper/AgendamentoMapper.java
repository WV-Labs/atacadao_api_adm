package com.mercado.sistema.dao.mapper;

import com.mercado.sistema.dao.dto.AgendamentoCreateRequest;
import com.mercado.sistema.dao.dto.AgendamentoResponse;
import com.mercado.sistema.dao.dto.AgendamentoUpdateRequest;
import com.mercado.sistema.dao.model.Agendamento;
import com.mercado.sistema.dao.model.Terminal;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    uses = {TerminalMapper.class})
public interface AgendamentoMapper {
  AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

  // Create Request -> Entity
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "usuario", ignore = true)

  // Será definido no controller
  @Mapping(target = "terminal", source = "terminalId", qualifiedByName = "terminalIdToTerminal")
  Agendamento toEntity(AgendamentoCreateRequest request);

  // Update Request -> Entity
  @Mapping(target = "usuario", ignore = true)
  @Mapping(target = "terminal", source = "terminalId", qualifiedByName = "terminalIdToTerminal")
  Agendamento toEntity(AgendamentoUpdateRequest request);

  // Entity -> Response
  @Mapping(target = "terminal", source = "terminal")
  @Mapping(target = "usuarioNome", source = "usuario.username")
  @Mapping(
      target = "duracaoMinutos",
      expression =
          "java(calcularDuracaoMinutos(agendamento.getDataInicio(), agendamento.getDataFim()))")
  @Mapping(
      target = "status",
      expression = "java(calcularStatus(agendamento.getDataInicio(), agendamento.getDataFim()))")
  AgendamentoResponse toResponse(Agendamento agendamento);

  // Update Entity com Request
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "usuario", ignore = true)
  @Mapping(target = "terminal", source = "terminalId", qualifiedByName = "terminalIdToTerminal")
  void updateEntityFromRequest(
      AgendamentoUpdateRequest request, @MappingTarget Agendamento agendamento);

  @Named("terminalIdToTerminal")
  default Terminal terminalIdToTerminal(Long terminalId) {
    if (terminalId == null) return null;
    Terminal terminal = new Terminal();
    terminal.setId(terminalId);
    return terminal;
  }

  // Métodos auxiliares para cálculos
  default Long calcularDuracaoMinutos(LocalDateTime inicio, LocalDateTime fim) {
    if (inicio == null || fim == null) return 0L;
    return java.time.Duration.between(inicio, fim).toMinutes();
  }

  default String calcularStatus(LocalDateTime inicio, LocalDateTime fim) {
    if (inicio == null || fim == null) return "INDEFINIDO";
    LocalDateTime agora = LocalDateTime.now();
    if (inicio.isAfter(agora)) {
      return "AGENDADO";
    } else if (fim.isBefore(agora)) {
      return "FINALIZADO";
    } else {
      return "EM_ANDAMENTO";
    }
  }

}
