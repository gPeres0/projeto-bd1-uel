package com.example.avaliacoes.controller;

import com.example.avaliacoes.model.Questionario;
import com.example.avaliacoes.model.Resultado;
import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.dto.QuestionarioExibicaoDTO;
import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.model.Usuario;
import com.example.avaliacoes.repository.QuestionarioRepository;
import com.example.avaliacoes.repository.ResultadoRepository;
import com.example.avaliacoes.service.QuestionarioService;
import com.example.avaliacoes.service.TemaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/questionarios")
public class QuestionarioController {

    private final QuestionarioService questionarioService;
    private final TemaService temaService;
    private final QuestionarioRepository questionarioRepository;
    private final ResultadoRepository resultadoRepository;

    public QuestionarioController(QuestionarioService questionarioService, TemaService temaService,
                                  QuestionarioRepository questionarioRepository, ResultadoRepository resultadoRepository) {
        this.questionarioService = questionarioService;
        this.temaService = temaService;
        this.questionarioRepository = questionarioRepository;
        this.resultadoRepository = resultadoRepository;
    }

    @GetMapping("/gerar")
    public String telaGerador(Model model) {
        model.addAttribute("temas", temaService.encontrarTodos());
        return "gerar-questionario";
    }

    @PostMapping("/preview")
    public String gerarPreview(@RequestParam Long temaId, @RequestParam String nome, Model model) {
        // Gera a sugestão aleatória
        Questionario preview = questionarioService.gerarSugestaoQuestionario(temaId, nome);
        
        // Verifica quantidade
        int qtdTotal = questionarioService.contarQuestoesPorTema(temaId);
        if (qtdTotal < 5) {
            model.addAttribute("avisoQuantidade", "Atenção: Este tema possui apenas " + qtdTotal + " questões cadastradas (o ideal são 5).");
        }

        model.addAttribute("temas", temaService.encontrarTodos());
        model.addAttribute("temaSelecionadoId", temaId);
        model.addAttribute("preview", preview);
        model.addAttribute("modoPreview", true);

        return "gerar-questionario";
    }

    // 3. Ação de Salvar Definitivo
    @PostMapping("/salvar")
    public String salvarQuestionario(@RequestParam String nome, @RequestParam Long temaId, @RequestParam(required = false) List<Long> questoesIds, 
                                     RedirectAttributes attributes, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";
        
        if (questoesIds == null || questoesIds.isEmpty()) {
            attributes.addFlashAttribute("erro", "Nenhuma questão selecionada.");
            return "redirect:/questionarios/gerar";
        }

        Questionario novoQuest = new Questionario();
        novoQuest.setNome(nome);
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(usuarioId);
        novoQuest.setUsuario(usuarioLogado);
        Tema t = new Tema();
        t.setId(temaId);
        novoQuest.setTema(t);
        
        // Reconstrói a lista de questões apenas com os IDs para salvar os relacionamentos
        List<Questao> listaQuestoes = new ArrayList<>();
        for (Long id : questoesIds) {
            Questao q = new Questao();
            q.setId(id);
            listaQuestoes.add(q);
        }
        novoQuest.setQuestoes(listaQuestoes);

        questionarioService.salvar(novoQuest);
        
        attributes.addFlashAttribute("sucesso", "Questionário '" + nome + "' criado com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/lista")
    public String listarQuestionarios(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // Busca DTO
        List<QuestionarioExibicaoDTO> lista = questionarioService.listarParaExibicao(usuarioId);
        
        model.addAttribute("listaQuestionarios", lista);
        
        return "lista-questionarios";
    }
    
    @GetMapping("/resolver/{id}")
    public String telaResolver(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("usuarioId") == null) return "redirect:/login";

        // Busca o questionário completo com questões e respostas
        Questionario q = questionarioRepository.findByIdCompleto(id);
        
        model.addAttribute("questionario", q);
        model.addAttribute("submetido", false);
        
        return "resolver-questionario";
    }

    @PostMapping("/resolver/{id}")
    public String processarResolucao(@PathVariable Long id, @RequestParam Map<String, String> formParams,
                                     Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        Questionario q = questionarioRepository.findByIdCompleto(id);
        int acertos = 0;
        int totalQuestoes = q.getQuestoes().size();

        // Itera sobre as questões para verificar acertos
        for (Questao questao : q.getQuestoes()) {
            // O form envia parâmetros como "resposta_10" -> "45" (id_questao -> id_resposta)
            String respostaSelecionadaIdStr = formParams.get("resposta_" + questao.getId());
            
            if (respostaSelecionadaIdStr != null) {
                Long respostaId = Long.parseLong(respostaSelecionadaIdStr);
                
                // Encontra a resposta correta no objeto carregado do banco
                boolean acertou = questao.getRespostas().stream()
                    .anyMatch(r -> r.getId().equals(respostaId) && r.getECorreta());
                
                if (acertou) acertos++;
            }
        }
        // Calcula nota (Escala 0 a 10)
        double notaFinal = (double) acertos / totalQuestoes * 10.0;
        // Salva o resultado no banco
        Resultado resultado = new Resultado();
        resultado.setNota(Math.round(notaFinal * 10.0) / 10.0);
        resultado.setData(LocalDateTime.now());
        
        Usuario u = new Usuario(); u.setId(usuarioId);
        resultado.setUsuario(u);
        
        Questionario qRef = new Questionario(); qRef.setId(id);
        resultado.setQuestionario(qRef);
        
        resultadoRepository.save(resultado);

        model.addAttribute("questionario", q);
        model.addAttribute("submetido", true);
        model.addAttribute("selecoesUsuario", formParams);
        model.addAttribute("placar", acertos + "/" + totalQuestoes);
        model.addAttribute("notaFinal", resultado.getNota());

        return "resolver-questionario";
    }
}