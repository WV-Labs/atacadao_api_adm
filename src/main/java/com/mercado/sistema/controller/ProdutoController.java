package com.mercado.sistema.controller;

import static com.mercado.sistema.general.Constants.ISEDIT;
import static com.mercado.sistema.general.MetodosGenericos.setModelAttributeErro;
import static com.mercado.sistema.service.ProdutoService.*;

import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.enums.UnidadeMedida;
import com.mercado.sistema.service.CategoriaService;
import com.mercado.sistema.service.ProdutoService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
  private static final String PRODUTO = "produtoRequest";
  private static final String CATEGORIAS = "categorias";
  public static final String PRODUTO_FORM = "produto/form";
  public static final String REDIRECT_PRODUTOS = "redirect:/produtos";
  public static final String PRODUTO_LIST = "produto/list";

  @Autowired
  private ProdutoService produtoService;
  @Autowired 
  private CategoriaService categoriaService;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("produtos", produtoService.listarTodos());
    return PRODUTO_LIST;
  }

  @GetMapping("/novo")
  public String novo(Model model) {
    setModelAttribute(model, new Produto(), false);
    return PRODUTO_FORM;
  }

  @GetMapping("/editar/{id}")
  public String editar(@PathVariable Long id, Model model) {
    Optional<Produto> produto = produtoService.findById(id);
    if (produto.isPresent()) {
      setModelAttribute(model, produto.get(), true);
      return PRODUTO_FORM;
    }
    return REDIRECT_PRODUTOS;
  }

  @PostMapping("/salvar")
  public String salvar(
        @Valid @ModelAttribute Produto produto,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {
      if (result.hasErrors()) {
        setModelAttribute(model, produto, true);
        setModelAttributeErro(result, model, "Produto");
        return PRODUTO_FORM;
      }

      if(produto.getCategoria().getId()== CAT_CAFETERIA || produto.getCategoria().getId()==CAT_PADARIA){
        produto.setImportado(false);
        produto.setEstoque(1);
      }else{
        if(produto.getCategoria().getId()==CAT_SEMCADASTRO){
          produto.setImportado(true);
        }
      }
    Produto save = produtoService.save(produto);

    if(produto.getCdTxtimport()==null){
      produto.setId(save.getId());
      produto.setCdTxtimport(90000L+save.getId());
      produtoService.save(produto);
    }

    redirectAttributes.addFlashAttribute("sucess", "Produto salvo com sucesso!");
    return REDIRECT_PRODUTOS;
  }

  @GetMapping("/excluir/{id}")
  public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    produtoService.deleteById(id);
    redirectAttributes.addFlashAttribute("sucess", "Produto excluído com sucesso!");
    return REDIRECT_PRODUTOS;
  }

  @PostMapping("/recarregar-cache")
  public String atualizarCache(Model model) {
    System.out.println("🧹♻️ Inicio carga Produtos");
    produtoService.obterListaProdutoRemotoGeral("Mercearia", 1);
    return REDIRECT_PRODUTOS;
  }

  private void setModelAttribute(Model model, Produto produto, boolean edit) {
    model.addAttribute(PRODUTO, produto);
    model.addAttribute(ISEDIT, edit);
    model.addAttribute(CATEGORIAS, categoriaService.findAll());
    model.addAttribute("unidadeMedidas", UnidadeMedida.values());
  }
}
