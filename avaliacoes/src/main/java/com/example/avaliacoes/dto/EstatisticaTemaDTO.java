package com.example.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstatisticaTemaDTO {
    private String tema;
    private Double mediaNota;
    private Integer totalQuestionarios;
}