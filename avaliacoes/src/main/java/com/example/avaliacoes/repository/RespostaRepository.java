package com.example.avaliacoes.repository;

import com.example.avaliacoes.model.Resposta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RespostaRepository {

    private final JdbcTemplate jdbc;

    public RespostaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapper para transformar linhas do ResultSet em objetos Resposta
    private final RowMapper<Resposta> respostaMapper = (rs, rowNum) -> {
        Resposta resposta = new Resposta();
        resposta.setId(rs.getLong("id"));
        resposta.setTexto(rs.getString("texto"));
        resposta.setECorreta(rs.getBoolean("e_correta")); 
        resposta.setQuestaoId(rs.getLong("questao_id"));
        return resposta;
    };

    // Busca todas as respostas vinculadas a uma questão específica.
    public List<Resposta> findByQuestaoId(Long questaoId) {
        String sql = "SELECT * FROM resposta WHERE questao_id = ?";
        return jdbc.query(sql, respostaMapper, questaoId);
    }

    // Busca uma resposta pelo seu ID próprio.
    public Resposta findById(Long id) {
        String sql = "SELECT * FROM resposta WHERE id = ?";
        return jdbc.queryForObject(sql, respostaMapper, id);
    }

    // Insere ou Atualiza uma resposta.
    public Resposta save(Resposta resposta) {
        if (resposta.getId() == null) {
            // INSERÇÃO
            String sql = "INSERT INTO resposta (texto, e_correta, questao_id) VALUES (?, ?, ?) RETURNING id";
            
            Long novoId = jdbc.queryForObject(sql, Long.class,
                resposta.getTexto(),
                resposta.getECorreta(),
                resposta.getQuestaoId()
            );
            resposta.setId(novoId);
        } else {
            // ATUALIZAÇÃO
            String sql = "UPDATE resposta SET texto = ?, e_correta = ?, questao_id = ? WHERE id = ?";
            
            jdbc.update(sql,
                resposta.getTexto(),
                resposta.getECorreta(),
                resposta.getQuestaoId(),
                resposta.getId()
            );
        }
        return resposta;
    }

    // Deleta todas as respostas de uma questão (útil para atualização de questionários/questões)
    public void deleteByQuestaoId(Long questaoId) {
        String sql = "DELETE FROM resposta WHERE questao_id = ?";
        jdbc.update(sql, questaoId);
    }
    
    // Deleta uma resposta específica
    public void deleteById(Long id) {
        String sql = "DELETE FROM resposta WHERE id = ?";
        jdbc.update(sql, id);
    }
}