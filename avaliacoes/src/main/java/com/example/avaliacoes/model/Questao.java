package com.example.avaliacoes.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Questao {
    private Long id;
    private String conteudo;
    private Tema tema;
    private List<Resposta> respostas = new ArrayList<>();

    // Construtor utilitário para inicializar lista
    public void adicionarResposta(Resposta resposta) {
        this.respostas.add(resposta);
    }
}