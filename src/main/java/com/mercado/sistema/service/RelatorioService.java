package com.mercado.sistema.service;

import com.mercado.sistema.dao.dto.*;
import com.mercado.sistema.dao.repository.CategoriaRepository;
import com.mercado.sistema.dao.repository.ProdutoRepository;
import com.mercado.sistema.dao.repository.TerminalRepository;
import com.mercado.sistema.dao.repository.VisualizacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {
  @Autowired
  private ProdutoRepository produtoRepository;
  @Autowired
  private TerminalRepository terminalRepository;
  @Autowired
  private VisualizacaoRepository visualizacaoRepository;
  @Autowired
  private CategoriaRepository categoriaRepository;

  public RelatorioVendasResponse gerarRelatorio(LocalDate inicio, LocalDate fim) {
    // Gerar dados simulados para demonstração
    // Em um sistema real, estes dados viriam de uma tabela de vendas
    // Resumo geral
    ResumoVendas resumoGeral =
        new ResumoVendas(
            new BigDecimal("15750.50"), // Total vendas
            127, // Quantidade vendas
            new BigDecimal("124.02"), // Ticket médio
            45, // Produtos vendidos
            89, // Clientes atendidos
            new BigDecimal("12.5") // Crescimento %
            );
    // Vendas por categoria
    List<VendaPorCategoria> vendasPorCategoria =
        List.of(
            new VendaPorCategoria(
                1L, "Açougue", new BigDecimal("5850.30"), 35, new BigDecimal("37.1"), 1),
            new VendaPorCategoria(
                2L, "Padaria", new BigDecimal("3920.80"), 42, new BigDecimal("24.9"), 2),
            new VendaPorCategoria(
                3L, "Hortifruti", new BigDecimal("2890.20"), 28, new BigDecimal("18.4"), 3),
            new VendaPorCategoria(
                4L, "Laticínios", new BigDecimal("1890.50"), 18, new BigDecimal("12.0"), 4),
            new VendaPorCategoria(
                5L, "Bebidas", new BigDecimal("1198.70"), 14, new BigDecimal("7.6"), 5));
    // Produtos mais vendidos
    List<ProdutoMaisVendido> produtosMaisVendidos =
        List.of(
            new ProdutoMaisVendido(
                1L,
                "Picanha Premium",
                "Açougue",
                25,
                new BigDecimal("2247.50"),
                new BigDecimal("89.90"),
                1,
                "7891000100001"),
            new ProdutoMaisVendido(
                4L,
                "Pão Francês",
                "Padaria",
                180,
                new BigDecimal("135.00"),
                new BigDecimal("0.75"),
                2,
                "7891000100004"),
            new ProdutoMaisVendido(
                7L,
                "Banana Prata",
                "Hortifruti",
                45,
                new BigDecimal("220.50"),
                new BigDecimal("4.90"),
                3,
                "7891000100007"),
            new ProdutoMaisVendido(
                10L,
                "Leite Integral",
                "Laticínios",
                32,
                new BigDecimal("159.68"),
                new BigDecimal("4.99"),
                4,
                "7891000100010"),
            new ProdutoMaisVendido(
                13L,
                "Coca-Cola 2L",
                "Bebidas",
                28,
                new BigDecimal("195.72"),
                new BigDecimal("6.99"),
                5,
                "7891000100013"));
    
    // Terminais mais acessados
    List<TerminalMaisAcessado> terminaisMaisAcessados =
        terminalRepository.findByAtivo(true).stream()
            .limit(5)
            .map(
                terminal -> {
                  Long visualizacoes = visualizacaoRepository.countByTerminal(terminal);
                  return new TerminalMaisAcessado(
                      terminal.getId(),
                      terminal.getNome(),
                      terminal.getLocalizacao(),
                      terminal.getCategoria().getNome(),
                      visualizacoes,
                      Math.round(visualizacoes * 0.15),
                      // Simular interações
                      Math.random() * 0.3 + 0.05, // Taxa conversão simulada
                      0, // Será preenchido depois
                      terminal.getUrl());
                })
            .toList();
    // Vendas por dia (últimos 7 dias)
    List<VendaPorDia> vendasPorDia =
        IntStream.range(0, 7)
            .mapToObj(
                i -> {
                  LocalDate data = fim.minusDays(i);
                  BigDecimal vendas = new BigDecimal(1500 + (Math.random() * 1000));
                  // Simular vendas
                  Integer quantidade = (int) (15 + (Math.random() * 25));
                  // Simular quantidade
                  return new VendaPorDia(data, vendas, quantidade);
                })
            .toList();
    return new RelatorioVendasResponse(
        inicio,
        fim,
        resumoGeral,
        vendasPorCategoria,
        produtosMaisVendidos,
        terminaisMaisAcessados,
        vendasPorDia,
        "Relatório gerado automaticamente com dados simulados para demonstração");
  }
  /** * Gerar relatório simplificado */

  public RelatorioVendasResponse gerarRelatorioSimples(LocalDate inicio, LocalDate fim) {
    ResumoVendas resumo = new ResumoVendas(new BigDecimal("8450.30"), 67);
    return new RelatorioVendasResponse(inicio, fim, resumo);
  }
  /** * Gerar relatório por categoria específica */

  public RelatorioVendasResponse gerarRelatorioPorCategoria(
      Long categoriaId, LocalDate inicio, LocalDate fim) {
    var categoria =
        categoriaRepository
            .findById(categoriaId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    // Implementar lógica específica da categoria
    ResumoVendas resumo = new ResumoVendas(new BigDecimal("3250.80"), 28);
    List<VendaPorCategoria> vendasCategoria =
        List.of(
            new VendaPorCategoria(
                categoriaId, categoria.getNome(), resumo.totalVendas(), resumo.quantidadeVendas()));
    return new RelatorioVendasResponse(
        inicio,
        fim,
        resumo,
        vendasCategoria,
        List.of(),
        List.of(),
        List.of(),
        "Relatório filtrado por categoria: " + categoria.getNome());
  }
}
