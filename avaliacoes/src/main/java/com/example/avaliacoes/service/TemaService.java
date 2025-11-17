package com.example.avaliacoes.service;

import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemaService {
    
    private final TemaRepository temaRepository;

    @Autowired
    public TemaService(TemaRepository temaRepository) {
        this.temaRepository = temaRepository;
    }

    public List<Tema> encontrarTodos() {
        return temaRepository.findAll();
    }

    public Tema salvarTema(String nome) {
        // Lógica simples de salvar um tema
        Tema novoTema = new Tema();
        novoTema.setNome(nome);
        return temaRepository.save(novoTema);
    }
    
    // Método para buscar pelo ID, útil para associar a questão
    public Tema buscarPorId(Long id) {
        return temaRepository.findById(id).orElse(null);
    }
}