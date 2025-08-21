package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AgendamentoCreateRequest(
    @NotBlank(message = "Título é obrigatório")
        @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres")
        String titulo,
    @Size(max = 500, message = "Descrição não pode exceder 500 caracteres") String descricao,
    @NotNull(message = "Data de início é obrigatória")
        @Future(message = "Data de início deve ser no futuro")
        LocalDateTime dataInicio,
    @NotNull(message = "Data de fim é obrigatória")
        @Future(message = "Data de fim deve ser no futuro")
    LocalDateTime dataFim,
    Long terminalId) {
  @AssertTrue(message = "Data de fim deve ser posterior à data de início")
  public boolean isDataFimValid() {
    if (dataInicio == null || dataFim == null) return true;
    return dataFim.isAfter(dataInicio);
  }

  @AssertTrue(message = "Agendamento não pode exceder 8 horas")
  public boolean isDuracaoValid() {
    if (dataInicio == null || dataFim == null) return true;
    return java.time.Duration.between(dataInicio, dataFim).toHours() <= 8;
  }
}
