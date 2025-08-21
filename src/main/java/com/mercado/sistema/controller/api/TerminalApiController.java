package com.mercado.sistema.controller.api;

import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.dao.model.Terminal;
import com.mercado.sistema.service.CategoriaService;
import com.mercado.sistema.service.ProdutoService;
import com.mercado.sistema.service.TerminalService;
import com.mercado.sistema.service.VisualizacaoService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class TerminalApiController {
  @Autowired
  private TerminalService terminalService;
  @Autowired
  private VisualizacaoService visualizacaoService;
  @Autowired
  private ProdutoService produtoService;
  @Autowired
  private CategoriaService categoriaService;

  @GetMapping("/{categoria}/{numero}")
  public ResponseEntity<?> getTerminalData(
          @PathVariable String categoria, @PathVariable Integer numero, HttpServletRequest request) {
    Optional<Terminal> terminal = terminalService.findByCategoriaNomeAndNumero(categoria, numero);
    if (!terminal.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Terminal terminalObj = terminal.get();
    List<Produto> produtos = produtoService.findByCategoria(terminalObj.getCategoria());

    // Registrar Visualização para cada produto
    String ip = request.getRemoteAddr();
    produtos.forEach(
            produto -> visualizacaoService.registrarVisualizacao(terminalObj, produto, ip));

    var response = new TerminalResponse();
    response.setTerminal(terminalObj);
    response.setProdutos(produtos);
    response.setTotalVisualizacoes(visualizacaoService.countByTerminal(terminalObj));

    return ResponseEntity.ok(response);
  }

  @GetMapping("/terminais")
  public ResponseEntity<List<Terminal>> getAllTerminals(HttpServletRequest request) {
    return ResponseEntity.ok(terminalService.findByAtivo(true));
  }

  @GetMapping("/produtos")
  public ResponseEntity<List<Produto>> getAllProdutos(HttpServletRequest request) {
    return ResponseEntity.ok(produtoService.listarTodos());
  }

  @GetMapping("/{categoria}")
  public ResponseEntity<List<Produto>> listaProdutosPorCategoria(@PathVariable Long id, Model model) {
    return ResponseEntity.ok(
            produtoService.findByProdutoCategoria(categoriaService.findById(id).orElse(null)));
  }

  public static class TerminalResponse {
    private Terminal terminal;
    private List<Produto> produtos;
    private Long totalVisualizacoes;

    public Terminal getTerminal() { return terminal;   }

    public void setTerminal(Terminal terminal) { this.terminal = terminal; }

    public List<Produto> getProdutos() { return produtos; }

    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }

    public Long getTotalVisualizacoes() { return totalVisualizacoes; }

    public void setTotalVisualizacoes(Long totalVisualizacoes) { this.totalVisualizacoes = totalVisualizacoes; }
  }
}
