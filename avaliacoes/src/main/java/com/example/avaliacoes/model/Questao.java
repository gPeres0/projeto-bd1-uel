package com.example.avaliacoes.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "questao")
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String conteudo;

    // Relacionamento N:1
    @ManyToOne
    @JoinColumn(name = "tema_id")
    private Tema tema;

    // Relacionamento N:M
    @ManyToMany(mappedBy = "questoes")
    private Set<Questionario> questionarios;

    // Relacionamento 1:N
    @OneToMany(mappedBy = "questao")
    private Set<Resposta> respostas;

//  

    public Questao() {}

//
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
//
    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
//
    public Tema getTema() {
        return tema;
    }

    public void setTema(Tema tema) {
        this.tema = tema;
    }
//
    public Set<Questionario> getQuestionario() {
        return questionarios;
    }

    public void setQuestionario(Set<Questionario> questionarios) {
        this.questionarios = questionarios;
    }
//
    public Set<Resposta> getRespostas() {
        return respostas;
    }    

    public void setRespostas(Set<Resposta> respostas) {
        this.respostas = respostas;
    }
}
