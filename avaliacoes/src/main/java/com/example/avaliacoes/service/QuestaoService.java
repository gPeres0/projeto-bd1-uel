package com.example.avaliacoes.service;

import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.repository.QuestaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestaoService {

    private final QuestaoRepository questaoRepository;

    @Autowired
    public QuestaoService(QuestaoRepository questaoRepository) {
        this.questaoRepository = questaoRepository;
    }

    public Questao salvarQuestao(Questao questao) {
        return questaoRepository.save(questao);
    }

    public List<Questao> encontrarTodas() {
        return questaoRepository.findAll();
    }
}