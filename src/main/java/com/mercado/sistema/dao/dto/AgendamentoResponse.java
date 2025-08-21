package com.mercado.sistema.dao.dto;

import java.time.LocalDateTime;

public record AgendamentoResponse(
    Long id,
    String titulo,
    String descricao,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    TerminalResponse terminal,
    String usuarioNome,
    Long duracaoMinutos,
    String status) {}
