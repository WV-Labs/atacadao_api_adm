package com.mercado.sistema.controller;

import static com.mercado.sistema.general.Constants.ISEDIT;
import static com.mercado.sistema.general.MetodosGenericos.setModelAttributeErro;

import com.mercado.sistema.dao.model.Agendamento;
import com.mercado.sistema.service.AgendamentoService;
import com.mercado.sistema.service.ConteudoService;
import com.mercado.sistema.service.TerminalService;
import com.mercado.sistema.service.UsuarioService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoController {
    private static final String AGENDAMENTO = "agendamentoRequest";
    private static final String AGENDAMENTO_FORM = "agendamento/form";
    private static final String TERMINAIS = "terminais";
    private static final String CONTEUDOS = "conteudos";
    private static final String REDIRECT_AGENDAMENTOS = "redirect:/agendamentos";

    @Autowired
    private AgendamentoService agendamentoService;
    @Autowired
    private TerminalService terminalService;
    @Autowired
    private ConteudoService conteudoService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(name = "origem", required = false) String origem) {
        model.addAttribute("agendamentos", agendamentoService.findAll());
        model.addAttribute("origem",  (origem==null?"/api-mercado/home": "/api-mercado" + origem));
        return "agendamento/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        setModelAttribute(model, new Agendamento(), false);
        return AGENDAMENTO_FORM;
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Agendamento> agendamento = agendamentoService.findById(id);
        if (agendamento.isPresent()) {
            setModelAttribute(model, agendamento.get(), true);
            return AGENDAMENTO_FORM;
        }
        return REDIRECT_AGENDAMENTOS;
    }

    @PostMapping("/salvar")
    @Transactional
    public String salvar(
            @Valid @ModelAttribute Agendamento agendamento,
            BindingResult result,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "conteudoSelect", required = false) Long conteudoSelecionado,
            @RequestParam(name = "terminalSelect", required = false) Long terminalSelecionado) {
        if (result.hasErrors()) {
            setModelAttribute(model, agendamento, true);
            setModelAttributeErro(result, model, "Agendamento");
            return AGENDAMENTO_FORM;
        }
        if (agendamentoService.temConflito(agendamento)) {
            setModelAttribute(model, agendamento, true);
            model.addAttribute("error", "Existe conflito de horário para esse terminal!");
            return AGENDAMENTO_FORM;
        }
        if(agendamento.getDataInicio()==null && agendamento.getDataFim() == null) {
            agendamento.setDataInicio(LocalDateTime.now());
            agendamento.setDataFim(LocalDateTime.of(2100, 1, 1, 0, 0));
        }
        if(agendamento.getDataFim() == null) {
            agendamento.setDataFim(LocalDateTime.of(2100, 1, 1, 0, 0));
        }
        if(agendamento.getId() == null || agendamento.getUsuario() == null) {
            var usuario = usuarioService.findByUsername(principal.getName()).orElse(null);
            agendamento.setUsuario(usuario);
        }
        if (agendamentoService.save(agendamento, conteudoSelecionado, terminalSelecionado)==null) {
            setModelAttribute(model, agendamento, true);
            model.addAttribute("error", "Agendamento NÃO foi salvo!");
            return AGENDAMENTO_FORM;
        }
        redirectAttributes.addFlashAttribute("sucess", "Agendamento salvo com sucesso!");
        return REDIRECT_AGENDAMENTOS;
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        agendamentoService.deleteById(id);
        redirectAttributes.addFlashAttribute("sucess", "Agendamento excluído com sucesso!");
        return REDIRECT_AGENDAMENTOS;
    }

    private void setModelAttribute(Model model, Agendamento agendamento, boolean edit) {
        if(!edit){
            agendamento.setDataFim(null);
            agendamento.setDataInicio(null);
        }else{
            if(agendamento.getDataFim().getYear()==2100){
                agendamento.setDataFim(null);
            }
        }
        model.addAttribute(AGENDAMENTO, agendamento);
        model.addAttribute(ISEDIT, edit);
        model.addAttribute(TERMINAIS, terminalService.findAll());
        model.addAttribute(CONTEUDOS, conteudoService.listarTodos());
        model.addAttribute("usuario_sist", usuarioService.findByUsername("admin").orElse(null));
    }
}
