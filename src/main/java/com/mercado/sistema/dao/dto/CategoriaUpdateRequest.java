package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoriaUpdateRequest(
    @NotNull(message = "ID é obrigatório para atualização")
    Long id,
    @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras e espaços")
        String nome,
    @Size(max = 255, message = "Descrição não pode exceder 255 caracteres")
    String descricao) {}
