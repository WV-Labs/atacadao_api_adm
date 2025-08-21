package com.mercado.sistema.controller;

import com.mercado.sistema.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private VisualizacaoService visualizacaoService;

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("totalProdutos", produtoService.listarTodos().size());
        model.addAttribute("totalCategorias", categoriaService.findAll().size());
        model.addAttribute("totalVisualizacoes", visualizacaoService.findAll().size());
        model.addAttribute("totalTerminais", terminalService.findAll().size());
        model.addAttribute("totalProdutosSemCategoria", produtoService.countByProdutoNaoAssociado());
        model.addAttribute("totalAgendamentos", agendamentoService.findAll().size());
        return "home";
    }
}
