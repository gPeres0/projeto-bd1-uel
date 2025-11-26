package com.example.avaliacoes.repository;

import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.model.Resposta;
import com.example.avaliacoes.model.Tema;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class QuestaoRepository {

    private final JdbcTemplate jdbc;

    public QuestaoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapper para Questão (trazendo dados do Tema via JOIN)
    private RowMapper<Questao> questaoRowMapper = (rs, rowNum) -> {
        Questao q = new Questao();
        q.setId(rs.getLong("id"));
        q.setConteudo(rs.getString("conteudo"));
        
        // Monta o objeto Tema dentro da Questão
        Tema t = new Tema();
        t.setId(rs.getLong("tema_id"));
        t.setNome(rs.getString("tema_nome"));
        q.setTema(t);
        
        return q;
    };

    public List<Questao> findAll() {
        // Fazemos um JOIN para pegar o nome do tema em uma única query
        String sql = "SELECT q.id, q.conteudo, q.tema_id, t.nome as tema_nome " +
                     "FROM questao q " +
                     "JOIN tema t ON q.tema_id = t.id";
        return jdbc.query(sql, questaoRowMapper);
    }

    @Transactional // Garante que Questão e Respostas sejam salvas juntas ou falhem juntas
    public void save(Questao questao) {
        Long questaoId = questao.getId();

        // 1. Salvar ou Atualizar a Questão
        if (questaoId == null) {
            String sql = "INSERT INTO questao (conteudo, tema_id) VALUES (?, ?) RETURNING id";
            questaoId = jdbc.queryForObject(sql, Long.class, 
                        questao.getConteudo(), 
                        questao.getTema().getId());
            questao.setId(questaoId);
        } else {
            jdbc.update("UPDATE questao SET conteudo = ?, tema_id = ? WHERE id = ?", 
                        questao.getConteudo(), 
                        questao.getTema().getId(), 
                        questaoId);
            // Limpa respostas antigas para recriar (estratégia simples)
            jdbc.update("DELETE FROM resposta WHERE questao_id = ?", questaoId);
        }

        // 2. Salvar as Respostas manualmente (Substitui o Cascade do JPA)
        if (questao.getRespostas() != null) {
            for (Resposta r : questao.getRespostas()) {
                jdbc.update("INSERT INTO resposta (texto, e_correta, questao_id) VALUES (?, ?, ?)",
                        r.getTexto(),
                        r.getECorreta(), // Boolean Getter correto
                        questaoId);      // Usa o ID da questão recém-salva
            }
        }
    }
}