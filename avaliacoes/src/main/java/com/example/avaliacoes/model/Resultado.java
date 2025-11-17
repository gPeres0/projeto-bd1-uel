package com.example.avaliacoes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultado")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double nota;

    private LocalDateTime data;

    // Relacionamento N:1
    @ManyToOne
    @JoinColumn(name = "id_user")
    private Usuario usuario;

    // Relacionamento N:1
    @ManyToOne
    @JoinColumn(name = "id_questionario")
    private Questionario questionario;

//

    public Resultado() {}

//
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
//
    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }
//
    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
//
    public Questionario getQuestionario() {
        return questionario;
    }

    public void setQuestionario(Questionario questionario) {
        this.questionario = questionario;
    }
}