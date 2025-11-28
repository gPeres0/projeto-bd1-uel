package com.example.avaliacoes.service;

import com.example.avaliacoes.dto.EstatisticaTemaDTO;
import com.example.avaliacoes.dto.GraficoPontoDTO;
import com.example.avaliacoes.repository.ResultadoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ResultadoRepository resultadoRepository;

    public DashboardService(ResultadoRepository resultadoRepository) {
        this.resultadoRepository = resultadoRepository;
    }

    public List<GraficoPontoDTO> getDadosGrafico(Long userId) {
        return resultadoRepository.findHistoricoGrafico(userId);
    }

    // Retorna um Map com "melhor" e "pior" tema
    public Map<String, EstatisticaTemaDTO> getMelhorPiorDesempenho(Long userId) {
        List<EstatisticaTemaDTO> stats = resultadoRepository.findEstatisticasPorTema(userId);
        Map<String, EstatisticaTemaDTO> resultado = new HashMap<>();

        if (!stats.isEmpty()) {
            resultado.put("melhor", stats.get(0));            
            resultado.put("pior", stats.get(stats.size() - 1));
        }
        return resultado;
    }
}