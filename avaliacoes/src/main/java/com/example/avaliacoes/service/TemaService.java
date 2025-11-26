package com.example.avaliacoes.service;

import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.repository.TemaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemaService {
    private final TemaRepository temaRepository;

    public TemaService(TemaRepository temaRepository) {
        this.temaRepository = temaRepository;
    }
    
    public List<Tema> encontrarTodos() { return temaRepository.findAll(); }
    public Tema buscarPorId(Long id) { return temaRepository.findById(id); }
    public Tema salvarTema(String nome) { return temaRepository.save(new Tema(null, nome)); }
}