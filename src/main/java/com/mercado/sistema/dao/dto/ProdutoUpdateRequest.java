package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoUpdateRequest(
        @NotNull(message = "ID é obrigatório para atualização")
        Long id,
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,
        @Size(max = 500, message = "Descrição não pode exceder 500 caracteres")
        String descricao,
        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "Preço inválido")
        BigDecimal preco,
        @Pattern(regexp = "^[0-9]{8,13}$", message = "Código de barras deve ter entre 8 e 13 dígitos")
        String codigoBarras,
        @NotNull(message = "Estoque é obrigatório")
        @Min(value = 0, message = "Estoque não pode ser negativo")
        @Max(value = 999999, message = "Estoque não pode exceder 999.999")
        Integer estoque, Long categoriaId,
        @Pattern(regexp = "^(https?://).*\\.(jpg|jpeg|png|gif|webp)$", message = "URL da imagem inválida")
        String imagem) {}
