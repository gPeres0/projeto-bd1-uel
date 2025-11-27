package com.example.avaliacoes.repository;

import com.example.avaliacoes.model.Tema;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

// import java.sql.ResultSet;
// import java.sql.SQLException;
import java.util.List;

@Repository
public class TemaRepository {

    private final JdbcTemplate jdbc;

    public TemaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Tema> temaMapper = (rs, rowNum) -> {
        return new Tema(rs.getLong("id"), rs.getString("nome"));
    };

    public List<Tema> findAll() {
        return jdbc.query("SELECT * FROM tema", temaMapper);
    }

    public Tema findById(Long id) {
        String sql = "SELECT * FROM tema WHERE id = ?";
        return jdbc.queryForObject(sql, temaMapper, id);
    }

    public Tema save(Tema tema) {
        if (tema.getId() == null) {
            // Inserção com retorno do ID gerado
            String sql = "INSERT INTO tema (nome) VALUES (?) RETURNING id";
            Long novoId = jdbc.queryForObject(sql, Long.class, tema.getNome());
            tema.setId(novoId);
        } else {
            jdbc.update("UPDATE tema SET nome = ? WHERE id = ?", tema.getNome(), tema.getId());
        }
        return tema;
    }
}