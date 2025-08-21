package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "Username é obrigatório")
        @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
        String username,
    @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String password,
    Boolean rememberMe) {
  public LoginRequest {
    // Compact constructor para valores padrão
    if (rememberMe == null) {
      rememberMe = false;
    }
  }

  // Construtor conveniência
  public LoginRequest(String username, String password) {
    this(username, password, false);
  }
}
