package com.mercado.sistema.controller.api.pbl;

import com.mercado.sistema.dao.dto.ProdutoDTO;
import com.mercado.sistema.dao.dto.ProdutoRemote;
import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.service.ProdutoService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
@PermitAll
public class ProdutoApiController {
  private static final String SUCCESS = "success";
  private static final String MESSAGE = "message";
  public static final String PRODUTO = "produto";
  @Autowired private ProdutoService produtoService;

  @GetMapping("/produtos-json")
  @PermitAll
  public ResponseEntity<List<ProdutoRemote>> getProdutosComoJson() {
    System.out.println("entrou no ADM");
    List<ProdutoRemote> produtosRemote = produtoService.obterListaProdutoRemotoGeral("semcadastro", 1);
    System.out.println("listagem");
    return ResponseEntity.ok(produtosRemote);
  }

  @GetMapping("/porCategoria/{codigo}")
  @PermitAll
  public @ResponseBody Map<String, List<Produto>> getProdutosPorCategoria(@PathVariable Long codigo) {
    System.out.println(" ********* ENTROU CARREGAR COMBO ***********");
    List<Produto> produtos = produtoService.findByCategoriaIdCustom(codigo);
    System.out.println(" ********* QTDE DE LINHAS =" + produtos.size());
    Map<String, List<Produto>> response = new HashMap<>();
    response.put("produtos", produtos);
    return response;
  }

  @GetMapping("/porCategoriaConteudo/{codigo}")
  @PermitAll
  public @ResponseBody Map<String, List<Produto>> getProdutosPorCategoriaConteudo(@PathVariable Long codigo) {
    System.out.println(" ********* ENTROU CARREGAR COMBO CONTEUDO***********");
    List<Produto> produtos = produtoService.findByCategoriaId(codigo);
    System.out.println(" ********* QTDE DE LINHAS =" + produtos.size());
    Map<String, List<Produto>> response = new HashMap<>();
    response.put("produtos", produtos);
    return response;
  }

  @GetMapping(value = "/produtos-json-remoto/{categoria}/{numero}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PermitAll
  public ResponseEntity<List<ProdutoRemote>> getProdutosComoJson(@PathVariable String categoria, @PathVariable Integer numero) {
    System.out.println("entrou no ADM - chamada da página do html");
    return ResponseEntity.ok(produtoService.obterListaProdutoRemotoEspecifico(categoria, numero));
  }

  /** Endpoint para salvar ou atualizar produto POST /api/import-txt */
  @PostMapping("/import-txt")
  @PermitAll
  public Map<String, Object> salvarProduto(@Valid @RequestBody List<ProdutoDTO> produtoDTOs) {
    return produtoService.buildImport(produtoDTOs);
  }

  /**
   * Endpoint para buscar produto por código de barras (para testes) GET
   * /api/public/produtos/codigo/{codigo}
   */
  @GetMapping("/id/{codigo}")
  public ResponseEntity<Map<String, Object>> buscarPorCodigo(@PathVariable String codigo) {
    Map<String, Object> response = new HashMap<>();

    try {
      Optional<Produto> produto = produtoService.findById(Long.parseLong(codigo));

      if (produto.isPresent()) {
        response.put(SUCCESS, true);
        response.put(PRODUTO, produtoService.criarRespostaProduto(produto.get()));
      } else {
        response.put(SUCCESS, false);
        response.put(MESSAGE, "Produto não encontrado com código: " + codigo);
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put(SUCCESS, false);
      response.put(MESSAGE, "Erro ao buscar produto: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

}
