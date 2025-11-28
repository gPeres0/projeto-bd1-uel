package com.example.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraficoPontoDTO {
    private String tema;
    private String data;
    private Double nota;
}