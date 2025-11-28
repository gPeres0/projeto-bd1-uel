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
    private final RespostaRepository respostaRepository; // Necessário para o preview das respostas

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
        
        // 1. Buscar todas as questões do tema
        List<Questao> todasQuestoes = questaoRepository.findByTemaId(temaId);
        
        // 2. Embaralhar a lista (Aleatoriedade)
        Collections.shuffle(todasQuestoes);
        
        // 3. Pegar as 5 primeiras (ou menos, se não tiver 5)
        List<Questao> selecionadas = todasQuestoes.stream()
            .limit(5)
            .collect(Collectors.toList());
            
        // 4. Carregar as respostas para essas questões (para o preview)
        for (Questao q : selecionadas) {
            List<Resposta> respostas = respostaRepository.findByQuestaoId(q.getId());
            q.setRespostas(respostas);
        }
        
        preview.setQuestoes(selecionadas);
        return preview;
    }
    
    // Método para contar questões (aviso)
    public int contarQuestoesPorTema(Long temaId) {
        return questaoRepository.findByTemaId(temaId).size();
    }
    
    public void salvar(Questionario q) {
        questionarioRepository.save(q);
    }

    public List<QuestionarioExibicaoDTO> listarParaExibicao(Long userId) {
        return questionarioRepository.findAllWithStatusForUser(userId);
    }
}