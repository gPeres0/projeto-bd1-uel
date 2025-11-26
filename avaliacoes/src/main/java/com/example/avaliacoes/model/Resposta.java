package com.example.avaliacoes.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Resposta {
    private Long id;
    private String texto;
    private Boolean eCorreta = false;
    private Long questaoId;
}