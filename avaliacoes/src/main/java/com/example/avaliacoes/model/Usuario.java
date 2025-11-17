package com.example.avaliacoes.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;

    // Relacionamento 1:N
    @OneToMany(mappedBy = "usuario")
    private Set<Questionario> questionariosRealizados;

    // Relacionamento 1:N
    @OneToMany(mappedBy = "usuario")
    private Set<Resultado> resultados;

//

    public Usuario() {}

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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
//
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
//
    public Set<Questionario> getQuestionariosRealizados() {
        return questionariosRealizados;
    }

    public void setQuestionariosRealizados(Set<Questionario> questionariosRealizados) {
        this.questionariosRealizados = questionariosRealizados;
    }
//
    public Set<Resultado> getResultados() {
        return resultados;
    }

    public void setResultados(Set<Resultado> resultados) {
        this.resultados = resultados;
    }
}