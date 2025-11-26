package com.example.avaliacoes.repository;

import com.example.avaliacoes.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importante para o método findByEmail

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Usuario> usuarioMapper = (rs, rowNum) -> {
        return new Usuario(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("senha")
        );
    };

    /**
     * Busca usuário por email.
     * Retorna um Optional para evitar NullPointerException.
     */
    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        return jdbc.query(sql, usuarioMapper, email).stream().findFirst();
    }

    public Usuario findById(Long id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        return jdbc.queryForObject(sql, usuarioMapper, id);
    }

    // Salva (Novo) ou Atualiza.
    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            // INSERÇÃO
            String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?) RETURNING id";
            Long novoId = jdbc.queryForObject(sql, Long.class, 
                usuario.getNome(), 
                usuario.getEmail(), 
                usuario.getSenha()
            );
            usuario.setId(novoId);
        } else {
            // ATUALIZAÇÃO
            String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE id = ?";
            jdbc.update(sql, 
                usuario.getNome(), 
                usuario.getEmail(), 
                usuario.getSenha(),
                usuario.getId()
            );
        }
        return usuario;
    }
}