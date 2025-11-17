package com.example.avaliacoes.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tema")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // Relacionamento N:M
    @ManyToMany(mappedBy = "temas")
    private Set<Questionario> questionarios;

    // Relacionamento 1:N
    @OneToMany(mappedBy = "tema")
    private Set<Questao> questoes;

//

    public Tema() {}

//
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
//
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
//
    public Set<Questionario> getQuestionarios() {
        return questionarios;
    }

    public void setQuestionarios(Set<Questionario> questionarios) {
        this.questionarios = questionarios;
    }
//
    public Set<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(Set<Questao> questoes) {
        this.questoes = questoes;
    }
}