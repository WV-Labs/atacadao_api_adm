package com.mercado.sistema.controller;

import static com.mercado.sistema.general.Constants.ISEDIT;
import static com.mercado.sistema.general.MetodosGenericos.setModelAttributeErro;

import com.mercado.sistema.dao.dto.ConteudoRequest;
import com.mercado.sistema.dao.model.Conteudo;
import com.mercado.sistema.dao.model.ConteudoTabelaPreco;
import com.mercado.sistema.service.*;
import com.mercado.sistema.service.ConteudoService;
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
@RequestMapping("/conteudos")
public class ConteudoController {
    private static final String CONTEUDO = "conteudoRequest";

    public static final String CONTEUDO_FORM = "conteudo/form";
    public static final String REDIRECT_CONTEUDOS = "redirect:/conteudos";
    public static final String CONTEUDO_LIST = "conteudo/list";

    private static final String CATEGORIAS = "categorias";

    @Autowired
    private ConteudoService conteudoService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private TerminalService terminalService;
    @Autowired
    private ConteudoTabelaPrecosService conteudoTabelaPrecosService;
    @Autowired
    private UploadProxyController uploadProxyController;

    @GetMapping
    public String list(Model model,
                       @RequestParam(name = "origem", required = false) String origem) {
        model.addAttribute("conteudos", conteudoService.listarTodos());
        model.addAttribute("origem",  (origem==null?"/api-mercado/home": "/api-mercado" + origem));
        return CONTEUDO_LIST;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        setModelAttribute(model, new Conteudo(), false);
        return CONTEUDO_FORM;
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Conteudo> conteudo = conteudoService.findById(id);
        if (conteudo.isPresent()) {
            List<ConteudoTabelaPreco> conteudoTabelaPrecos = conteudoTabelaPrecosService.getConteudoTabelaPrecos(conteudo.get());
            if(!conteudoTabelaPrecos.isEmpty()) {
                List<String> ids = conteudoTabelaPrecos.stream()
                        .map(conteudoTabelaPreco -> String.valueOf(conteudoTabelaPreco.getProduto().getId()))
                        .collect(Collectors.toList());
                String produtosSelecionados = String.join(",", ids);
                conteudo.get().setProdutosSelecionados(produtosSelecionados);
                model.addAttribute("categoriaSelecionada", categoriaService.findById(conteudoTabelaPrecos.get(0).getProduto().getCategoria().getId()).get());
            }else{
                model.addAttribute("categoriaSelecionada", null);
            }
            setModelAttribute(model, conteudo.get(), true);
            return CONTEUDO_FORM;
        }
        return REDIRECT_CONTEUDOS;
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute ConteudoRequest conteudoRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== INICIO DO METODO SALVAR ===");
        System.out.println("ConteudoRequest: " + conteudoRequest);
        System.out.println("Nome da mídia: " + conteudoRequest.getNomeMidia());

        try {
            Conteudo conteudo = conteudoService.getConteudo(conteudoRequest);

            if (result.hasErrors()) {
                System.out.println("Erros de validação encontrados");
                setModelAttribute(model, conteudo, conteudoRequest.getId() != null);
                setModelAttributeErro(result, model, "Conteudo");
                return CONTEUDO_FORM;
            }
            if (conteudoRequest.getNomeMidia() != null && !conteudoRequest.getNomeMidia().trim().isEmpty()) {
                conteudo.setNomeMidia(conteudoRequest.getNomeMidia());
                System.out.println("Arquivo de mídia associado: " + conteudoRequest.getNomeMidia());
            } else {
                System.out.println("Nenhum arquivo de mídia associado");
            }

            // 5. Salvar o conteúdo
            System.out.println("Salvando conteúdo: " + conteudo);
            conteudoService.salvar(conteudo);

            redirectAttributes.addFlashAttribute("sucess", "Conteúdo salvo com sucesso!");
            return REDIRECT_CONTEUDOS;

        } catch (Exception e) {
            e.printStackTrace();

            // Se deu erro e já fez upload, tentar remover o arquivo
            if (conteudoRequest.getNomeMidia() != null && !conteudoRequest.getNomeMidia().isEmpty()) {
                try {
                    uploadProxyController.deletarArquivo(conteudoRequest.getNomeMidia());
                } catch (Exception ex) {
                    System.err.println("Erro ao remover arquivo após falha: " + ex.getMessage());
                }
            }

            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar conteúdo: " + e.getMessage());
            return "redirect:/conteudos/novo";
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Buscar conteúdo para obter nome do arquivo
            Conteudo conteudo = conteudoService.findConteudoById(id);

            // Remover arquivo se existir
            if (conteudo.getNomeMidia() != null && !conteudo.getNomeMidia().isEmpty()) {
                try {
                    uploadProxyController.deletarArquivo(conteudo.getNomeMidia());
                    System.out.println("Arquivo removido: " + conteudo.getNomeMidia());
                } catch (Exception ex) {
                    System.err.println("Erro ao remover arquivo: " + ex.getMessage());
                    // Continua com a exclusão mesmo se der erro no arquivo
                }
            }

            conteudoService.deleteById(id);
            redirectAttributes.addFlashAttribute("sucess", "Conteúdo excluído com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir conteúdo: " + e.getMessage());
        }

        return REDIRECT_CONTEUDOS;
    }

    private void setModelAttribute(Model model, Conteudo conteudo, boolean edit) {
        model.addAttribute(CONTEUDO, conteudo);
        model.addAttribute(ISEDIT, edit);
        model.addAttribute(CATEGORIAS, categoriaService.findAll());
    }
}