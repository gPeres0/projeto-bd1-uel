package com.example.avaliacoes.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Questionario {

    private Long id;
    private String nome;
    // Relacionamento N:1
    private Usuario usuario;
    // Relacionamento N:1
    private Tema tema;
    // Relacionamento N:M
    private List<Questao> questoes = new ArrayList<>();
    // Relacionamento 1:N
    private List<Resultado> resultados = new ArrayList<>();
}