package com.example.avaliacoes.service;

import com.example.avaliacoes.dto.QuestionarioExibicaoDTO;
import com.example.avaliacoes.model.*;
import com.example.avaliacoes.repository.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionarioService {

    private final QuestionarioRepository questionarioRepository;
    private final QuestaoRepository questaoRepository;
    private final RespostaRepository respostaRepository;

    public QuestionarioService(QuestionarioRepository questionarioRepository, 
                               QuestaoRepository questaoRepository,
                               RespostaRepository respostaRepository) {
        this.questionarioRepository = questionarioRepository;
        this.questaoRepository = questaoRepository;
        this.respostaRepository = respostaRepository;
    }

    // Gera um objeto Questionario com questões aleatórias.
    public Questionario gerarSugestaoQuestionario(Long temaId, String nomeQuestionario) {
        Questionario preview = new Questionario();
        preview.setNome(nomeQuestionario);

        Tema tema = new Tema();
        tema.setId(temaId);
        // tema = temaRepository.findById(temaId);
        preview.setTema(tema);
        
        List<Questao> todasQuestoes = questaoRepository.findByTemaId(temaId);
        
        Collections.shuffle(todasQuestoes);
        
        List<Questao> selecionadas = todasQuestoes.stream()
            .limit(5)
            .collect(Collectors.toList());
            
        for (Questao q : selecionadas) {
            List<Resposta> respostas = respostaRepository.findByQuestaoId(q.getId());
            q.setRespostas(respostas);
        }
        
        preview.setQuestoes(selecionadas);
        return preview;
    }
    
    public int contarQuestoesPorTema(Long temaId) {
        return questaoRepository.findByTemaId(temaId).size();
    }
    
    public void salvar(Questionario q) {
        questionarioRepository.save(q);
    }

    public List<QuestionarioExibicaoDTO> listarParaExibicao(Long userId) {
        return questionarioRepository.findAllWithStatusForUser(userId);
    }

    public void excluir(Long id) {
        questaoRepository.deleteById(id);
    }
}