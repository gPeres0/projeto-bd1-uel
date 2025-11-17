package com.example.avaliacoes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.avaliacoes.model.Questao;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {}
