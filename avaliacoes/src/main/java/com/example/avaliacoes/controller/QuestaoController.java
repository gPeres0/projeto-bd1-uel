package com.example.avaliacoes.controller;

import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.model.Resposta;
import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.service.QuestaoService;
import com.example.avaliacoes.service.TemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Questao questao = new Questao();
        for (int i = 0; i < 4; i++) {
            questao.getRespostas().add(new Resposta());
        }
        model.addAttribute("questao", questao);
        
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

    @GetMapping("/excluir/{id}")
    public String excluirQuestao(@PathVariable Long id, RedirectAttributes attr) {
        questaoService.excluir(id);
        attr.addFlashAttribute("mensagemSucesso", "Questão excluída com sucesso.");
        return "redirect:/questoes/lista";
    }

    @GetMapping("/editar/{id}")
    public String editarQuestao(@PathVariable Long id, Model model) {
        Questao questao = questaoService.buscarPorId(id);
        
        model.addAttribute("questao", questao);
        model.addAttribute("temas", temaService.encontrarTodos());
        model.addAttribute("novoTema", new com.example.avaliacoes.model.Tema());
        
        return "cadastro-questao";
    }
}