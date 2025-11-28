package com.example.avaliacoes.controller;

import com.example.avaliacoes.dto.GraficoPontoDTO;
import com.example.avaliacoes.dto.EstatisticaTemaDTO;
import com.example.avaliacoes.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/desempenho")
    public String telaDesempenho(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) return "redirect:/login";

        List<GraficoPontoDTO> dadosGrafico = dashboardService.getDadosGrafico(usuarioId);
        model.addAttribute("dadosGrafico", dadosGrafico);

        Map<String, EstatisticaTemaDTO> stats = dashboardService.getMelhorPiorDesempenho(usuarioId);
        model.addAttribute("melhorTema", stats.get("melhor"));
        model.addAttribute("piorTema", stats.get("pior"));

        return "desempenho-usuario";
    }
}