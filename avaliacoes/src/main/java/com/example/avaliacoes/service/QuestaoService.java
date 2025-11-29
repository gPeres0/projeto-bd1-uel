package com.example.avaliacoes.service;

import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.repository.QuestaoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestaoService {
    private final QuestaoRepository questaoRepository;

    public QuestaoService(QuestaoRepository questaoRepository) {
        this.questaoRepository = questaoRepository;
    }

    public List<Questao> encontrarTodas() {
        return questaoRepository.findAll();
    }

    public Questao buscarPorId(Long id) {
        return questaoRepository.findById(id);
    }

    public void salvarQuestao(Questao questao) {
        questaoRepository.save(questao);
    }

    public void excluir(Long id) {
        questaoRepository.deleteById(id);
    }
}