package com.example.avaliacoes.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "questionario")
public class Questionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer nota;

    // Relacionamento N:1
    @ManyToOne
    @JoinColumn(name = "id_user")
    private Usuario usuario;

    // Relacionamento N:M
    @ManyToMany
    @JoinTable(
        name = "questionario_tema",
        joinColumns = @JoinColumn(name = "questionario_id"),
        inverseJoinColumns = @JoinColumn(name = "tema_id")
    )
    private Set<Tema> temas;

    // Relacionamento N:M
    @ManyToMany
    @JoinTable(
        name = "questionario_questao",
        joinColumns = @JoinColumn(name = "questionario_id"),
        inverseJoinColumns = @JoinColumn(name = "questao_id")
    )
    private Set<Questao> questoes;

    // Relacionamento 1:N
    @OneToMany(mappedBy = "questionario")
    private Set<Resultado> resultados;

//
    
    public Questionario() {}

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
    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }
//
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
//
    public Set<Tema> getTemas() {
        return temas;
    }

    public void setTemas(Set<Tema> temas) {
        this.temas = temas;
    }
//
    public Set<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(Set<Questao> questoes) {
        this.questoes = questoes;
    }
}