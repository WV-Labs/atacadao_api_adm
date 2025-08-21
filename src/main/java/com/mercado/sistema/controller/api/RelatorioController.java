package com.mercado.sistema.controller.api;

import com.mercado.sistema.dao.dto.RelatorioVendasResponse;
import com.mercado.sistema.service.RelatorioService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
  @Autowired
  private RelatorioService relatorioService;

  @GetMapping("/vendas")
  public ResponseEntity<RelatorioVendasResponse> relatorioVendas(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
    RelatorioVendasResponse relatorio = relatorioService.gerarRelatorio(inicio, fim);
    return ResponseEntity.ok(relatorio);
  }

  @GetMapping("/vendas/simples")
  public ResponseEntity<RelatorioVendasResponse> relatorioVendasSimples(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
    RelatorioVendasResponse relatorio = relatorioService.gerarRelatorioSimples(inicio, fim);
    return ResponseEntity.ok(relatorio);
  }

  @GetMapping("/vendas/categoria/{categoriaId}")
  public ResponseEntity<RelatorioVendasResponse> relatorioVendasPorCategoria(
      @PathVariable Long categoriaId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
    RelatorioVendasResponse relatorio =
        relatorioService.gerarRelatorioPorCategoria(categoriaId, inicio, fim);
    return ResponseEntity.ok(relatorio);
  }
}
