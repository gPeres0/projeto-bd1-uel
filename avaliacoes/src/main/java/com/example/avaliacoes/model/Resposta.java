package com.example.avaliacoes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "resposta")
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texto;

    @Column(name = "e_correta")
    private Boolean eCorreta;

    // Relacionamento N:1
    @ManyToOne
    @JoinColumn(name = "id_quest")
    private Questao questao;

//

    public Resposta() {}

//
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
//
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
//
    public Boolean getCorreta() {
        return eCorreta;
    }

    public void setCorreta(Boolean eCorreta) {
        this.eCorreta = eCorreta;
    }
//
    public Questao getQuestao() {
        return questao;
    }

    public void setQuestao(Questao questao) {
        this.questao = questao;
    }
}