package com.mercado.sistema.controller;

import static com.mercado.sistema.general.Constants.ISEDIT;
import static com.mercado.sistema.general.MetodosGenericos.setModelAttributeErro;

import com.mercado.sistema.dao.model.Terminal;
import com.mercado.sistema.enums.UnidadeMedida;
import com.mercado.sistema.service.CategoriaService;
import com.mercado.sistema.service.ConteudoService;
import com.mercado.sistema.service.ProdutoService;
import com.mercado.sistema.service.TerminalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/terminais")
public class TerminalController {
  private static final String TERMINAL = "terminalRequest";
  private static final String TERMINAL_FORM = "terminal/form";
  private static final String CATEGORIAS = "categorias";
  private static final String REDIRECT_TERMINAIS = "redirect:/terminais";
  public static final String TERMINAL_LIST = "terminal/list";
  
  @Autowired
  private TerminalService terminalService;
  @Autowired
  private CategoriaService categoriaService;
  @Autowired
  private ProdutoService produtoService;
  @Autowired
  private ConteudoService conteudoService;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("terminais", terminalService.findAll());
    return TERMINAL_LIST;
  }

  @GetMapping("/novo")
  public String novo(Model model) {
    setModelAttribute(model, new Terminal(), false);
    return TERMINAL_FORM;
  }

  @GetMapping("/editar/{id}")
  public String editar(@PathVariable Long id, Model model) {
    Optional<Terminal> terminal = terminalService.findById(id);
    if (terminal.isPresent()) {
      setModelAttribute(model, terminal.get(), true);
      return TERMINAL_FORM;
    }
    return REDIRECT_TERMINAIS;
  }

  @PostMapping("/salvar")
  public String salvar(
        @Valid @ModelAttribute Terminal terminal,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes,
        @RequestParam(name = "produtosSelecionados", required = false) List<Long> produtosSelecionados){
      if (result.hasErrors()) {
        setModelAttribute(model, terminal, true);
        setModelAttributeErro(result, model, "Terminal");
        return TERMINAL_FORM;
      }
    var categoria = categoriaService.findByNome(terminal.getCategoria().getNome()).orElse(null);
    Optional<Terminal> terminalFind = terminalService
            .findByCategoriaNomeAndNumero(categoria.getNome(), terminal.getNrTerminal());
    if(terminalFind.isPresent()){
      terminal.setId(terminalFind.get().getId());
    }
    if(terminalService.save(terminal)==null){
        setModelAttribute(model, terminal, true);
        setModelAttributeErro(result, model, "Terminal");
        model.addAttribute(
                "erro", "Combinação 'Departamento/Nr Terminal' já existe" + "\\n  ATENÇÃO não foi salvo!");
        return TERMINAL_FORM;
      }
      redirectAttributes.addFlashAttribute("sucess", "Terminal salvo com sucesso!");
      return REDIRECT_TERMINAIS;
  }

  @GetMapping("/excluir/{id}")
  public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    terminalService.deleteById(id);
    redirectAttributes.addFlashAttribute("sucess", "Terminal excluído com sucesso!");
    return REDIRECT_TERMINAIS;
  }

  @GetMapping("/{categoria}/{numero}")
  public String getTerminalData(
          @PathVariable String categoria, @PathVariable Integer numero, HttpServletRequest request,
          Model model) {
    model.addAttribute("categoria", categoria);
    model.addAttribute("numero", numero);
    Optional<Terminal> terminal = terminalService.findByCategoriaNomeAndNumero(categoria, numero);
    if (!terminal.isPresent()) {
      return "naoEncontrada";
    }
    return "redirect:http://localhost:8081/api-tv/" + categoria + "/" + numero;
  }

  @PostMapping("/recarregar-cache")
  public String atualizarCache(Model model) {
    System.out.println("🧹♻️ Inicio carga Produtos");
    produtoService.obterListaProdutoRemotoGeral("semcadastro", 1);
    return REDIRECT_TERMINAIS;
  }

  private void setModelAttribute(Model model, Terminal terminal, boolean edit) {
    model.addAttribute(TERMINAL, terminal);
    model.addAttribute(ISEDIT, edit);
    model.addAttribute(CATEGORIAS, categoriaService.findAll());
    model.addAttribute("unidadeMedidas", UnidadeMedida.values());
    List<Long> produtoId = conteudoService.findProdutoId(terminal.getId());
    model.addAttribute("produtosSelecionados", (produtoId.isEmpty() ? Collections.emptyList() : produtoId));
  }

}
