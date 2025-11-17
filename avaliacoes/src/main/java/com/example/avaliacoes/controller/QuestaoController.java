package com.example.avaliacoes.controller;

import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.service.QuestaoService;
import com.example.avaliacoes.service.TemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questoes")
public class QuestaoController {

    private final QuestaoService questaoService;
    private final TemaService temaService;

    @Autowired
    public QuestaoController(QuestaoService questaoService, TemaService temaService) {
        this.questaoService = questaoService;
        this.temaService = temaService;
    }

    /*
     * Mapeia para a URL /questoes/nova (GET)
     * Exibe o formulário de cadastro
     */
    @GetMapping("/nova")
    public String exibirFormulario(Model model) {
        model.addAttribute("questao", new Questao());
        model.addAttribute("temas", temaService.encontrarTodos());
        model.addAttribute("novoTema", new Tema());
        return "cadastro-questao";
    }

    /*
     * Mapeia para a URL /questoes/nova (POST)
     * Recebe e salva a nova Questao
     */
    @PostMapping("/nova")
    public String salvarQuestao(@ModelAttribute("questao") Questao questao, String novoTemaNome) {
        if (novoTemaNome != null && !novoTemaNome.isBlank()) {
            Tema novoTema = temaService.salvarTema(novoTemaNome.trim());
            questao.setTema(novoTema); // Associa o novo tema à questão
        
        } else if (questao.getTema() != null && questao.getTema().getId() != null) {
            // 2. Lógica para usar um Tema EXISTENTE, se um ID foi selecionado.
            // O Spring já deve ter anexado o Tema pelo ID, mas garantimos
            Tema temaExistente = temaService.buscarPorId(questao.getTema().getId());
            questao.setTema(temaExistente);
        }

        questaoService.salvarQuestao(questao);
        return "redirect:/questoes/nova?sucesso";
    }

    @GetMapping("/lista")
    public String listarQuestoes(Model model) {
        // Lógica para buscar e exibir todas as questões.
        model.addAttribute("questoes", questaoService.encontrarTodas());
        return "lista-questoes";
    }
}