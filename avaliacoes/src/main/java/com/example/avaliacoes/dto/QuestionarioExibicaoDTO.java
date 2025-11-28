package com.example.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionarioExibicaoDTO {
    private Long id;
    private String titulo;
    private String tema;
    private Double nota;
    private LocalDateTime dataResolucao;
    
    // Método auxiliar para o HTML saber se está resolvido
    public boolean isResolvido() {
        return nota != null;
    }
}