package com.example.avaliacoes.service;

import com.example.avaliacoes.model.Usuario;
import com.example.avaliacoes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        // 1. Buscar o usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // 2. Verificar se a senha corresponde (em um sistema real, usaria criptografia como BCrypt)
            if (usuario.getSenha().equals(senha)) {
                return usuarioOpt; // Autenticação bem-sucedida
            }
        }
        return Optional.empty(); // Autenticação falhou
    }

    public Usuario salvarUsuario(Usuario usuario) {
        // Em um sistema real, você deveria:
        // 1. Verificar se o email já existe (para evitar duplicidade)
        // 2. Criptografar a senha (usando BCryptEncoder)
        
        // Se o usuário já tiver um ID, o JPA fará um UPDATE.
        // Se não tiver ID (novo cadastro), o JPA fará um INSERT.
        return usuarioRepository.save(usuario);
    }

}