package com.mercado.sistema.controller;

import static com.mercado.sistema.general.Constants.ISEDIT;
import static com.mercado.sistema.general.MetodosGenericos.setModelAttributeErro;

import com.mercado.sistema.dao.model.Categoria;
import com.mercado.sistema.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
  private static final String CATEGORIA = "categoriaRequest";
  private static final String CATEGORIA_FORM = "categoria/form";
  private static final String REDIRECT_CATEGORIAS = "redirect:/categorias";

  @Autowired
  private CategoriaService categoriaService;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("categorias", categoriaService.findAll());
    return "categoria/list";
  }

  @GetMapping("/novo")
  public String novo(Model model) {
    setModelAttribute(model, new Categoria(), false);
    return CATEGORIA_FORM;
  }

  @GetMapping("/editar/{id}")
  public String editar(@PathVariable Long id, Model model) {
    Optional<Categoria> categoria = categoriaService.findById(id);
    if (categoria.isPresent()) {
      List<String> ids =
              categoria.stream()
                      .flatMap(c -> c.getProdutos().stream())
                      .map(p -> String.valueOf(p.getId()))
                      .collect(Collectors.toList());

      String produtosSelecionados = String.join(",", ids);
      categoria.get().setProdutosSelecionados(produtosSelecionados);

      setModelAttribute(model, categoria.get(), true);
      return CATEGORIA_FORM;
    }
    return REDIRECT_CATEGORIAS;
  }

  @PostMapping("/salvar")
  public String salvar(
        @Valid @ModelAttribute Categoria categoria,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {
      if (result.hasErrors()) {
        setModelAttribute( model, categoria, true);
        setModelAttributeErro(result, model, "Departamento");
        return CATEGORIA_FORM;
      }
      if(categoriaService.save(categoria)==null){
        setModelAttribute( model, categoria, true);
        setModelAttributeErro(result, model, "Departamento");
        model.addAttribute(
                "erro", "Departamento já existe" + "\\n  ATENÇÃO não foi salvo!");
        return CATEGORIA_FORM;
      }
      redirectAttributes.addFlashAttribute("sucess", "Departamento salvo com sucesso!");
      return REDIRECT_CATEGORIAS;
  }

  @GetMapping("/excluir/{id}")
  public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    categoriaService.deleteById(id);
    redirectAttributes.addFlashAttribute("sucess", "Departamento excluído com sucesso!");
    return REDIRECT_CATEGORIAS;
  }

  private void setModelAttribute(Model model, Categoria categoria, boolean edit) {
    model.addAttribute(CATEGORIA, categoria);
    model.addAttribute(ISEDIT, edit);
  }
  
}
