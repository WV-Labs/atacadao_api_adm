package com.mercado.sistema.controller.api;

import com.mercado.sistema.dao.dto.ProdutoResponse;
import com.mercado.sistema.dao.dto.RelatorioVendasResponse;
import com.mercado.sistema.dao.mapper.ProdutoMapper;
import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.service.ProdutoService;
import java.time.LocalDate;
import java.util.List;

import com.mercado.sistema.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/custom")
public class CustomApiController {
  @Autowired
  private ProdutoService produtoService;
  @Autowired
  private RelatorioService relatorioService;
  private ProdutoMapper produtoMapper;

  @GetMapping("/produtos-em-promocao")
  public ResponseEntity<List<ProdutoResponse>> produtosEmPromocao() {
    // Sua lógica personalizada
    List<Produto> produtos = produtoService.findProdutosEmPromocao();
    List<ProdutoResponse> response = produtos.stream().map(produtoMapper::toResponse).toList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/relatorio-vendas")
  public ResponseEntity<RelatorioVendasResponse> relatorioVendas(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
    // Implementar lógica de relatório
    RelatorioVendasResponse relatorio = relatorioService.gerarRelatorio(inicio, fim);
    return ResponseEntity.ok(relatorio);
  }
}
