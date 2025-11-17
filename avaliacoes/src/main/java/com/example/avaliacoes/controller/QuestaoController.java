package com.example.avaliacoes.controller;

import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.repository.QuestaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questoes")
public class QuestaoController {

    private final QuestaoRepository repository;

    public QuestaoController(QuestaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("questoes", repository.findAll());
        return "questoes-list";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("questao", new Questao());
        return "questoes-form";
    }

    @PostMapping
    public String salvar(Questao questao) {
        repository.save(questao);
        return "redirect:/questoes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("questao", repository.findById(id).orElse(null));
        return "questoes-form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/questoes";
    }
}
