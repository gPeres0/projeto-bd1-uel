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
        // Buscar o usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Verificar se a senha corresponde
            if (usuario.getSenha().equals(senha)) {
                return usuarioOpt; // Autenticação bem-sucedida
            }
        }
        return Optional.empty(); // Autenticação falhou
    }

    public Usuario salvarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

}