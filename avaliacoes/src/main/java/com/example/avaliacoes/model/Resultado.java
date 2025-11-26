package com.example.avaliacoes.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resultado {

    private Long id;
    private Double nota;
    private LocalDateTime data;

    // Relacionamentos N:1
    private Usuario usuario;
    private Questionario questionario;
}