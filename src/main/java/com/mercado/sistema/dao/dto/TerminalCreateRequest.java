package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.*;

public record TerminalCreateRequest(
    @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,
    @NotBlank(message = "Localização é obrigatória")
        @Size(min = 3, max = 100, message = "Localização deve ter entre 3 e 100 caracteres")
        String localizacao,
    @NotNull(message = "Categoria é obrigatória") Long categoriaId,
    @NotNull(message = "Número é obrigatório")
        @Min(value = 1, message = "Número deve ser maior que zero")
        @Max(value = 999, message = "Número não pode exceder 999")
        Integer numero,
    Boolean ativo) {
  public TerminalCreateRequest {
    if (ativo == null) {
      ativo = true;
    }
  }
}
