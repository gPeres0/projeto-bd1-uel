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
        String sql = """
            SELECT q.*, t.nome as tema_nome 
            FROM questao q 
            LEFT JOIN tema t ON q.tema_id = t.id
            ORDER BY q.id
        """;
        
        List<Questao> lista = jdbc.query(sql, (rs, rowNum) -> {
            Questao q = new Questao();
            q.setId(rs.getLong("id"));
            q.setConteudo(rs.getString("conteudo"));
            
            Tema t = new Tema();
            t.setId(rs.getLong("tema_id"));
            try { t.setNome(rs.getString("tema_nome")); } catch (Exception e) {}
            q.setTema(t);
            
            return q;
        });

        for (Questao q : lista) {
            String sqlResp = "SELECT * FROM resposta WHERE questao_id = ?";
            List<Resposta> respostas = jdbc.query(sqlResp, (rs, rn) -> {
                Resposta r = new Resposta();
                r.setId(rs.getLong("id"));
                r.setTexto(rs.getString("texto"));
                return r;
            }, q.getId());
            q.setRespostas(respostas);
        }

        return lista;
    }

    public Questao findById(Long id) {
        String sqlQuestao = """
            SELECT q.*, t.nome as tema_nome 
            FROM questao q 
            LEFT JOIN tema t ON q.tema_id = t.id 
            WHERE q.id = ?
        """;

        Questao q = jdbc.queryForObject(sqlQuestao, (rs, rowNum) -> {
            Questao obj = new Questao();
            obj.setId(rs.getLong("id"));
            obj.setConteudo(rs.getString("conteudo"));
            
            com.example.avaliacoes.model.Tema t = new com.example.avaliacoes.model.Tema();
            t.setId(rs.getLong("tema_id"));
            try { t.setNome(rs.getString("tema_nome")); } catch (Exception e) {}
            obj.setTema(t);
            
            return obj;
        }, id);

        if (q != null) {
            String sqlRespostas = "SELECT * FROM resposta WHERE questao_id = ?"; 
            
            List<com.example.avaliacoes.model.Resposta> respostas = jdbc.query(sqlRespostas, (rs, rowNum) -> {
                com.example.avaliacoes.model.Resposta r = new com.example.avaliacoes.model.Resposta();
                r.setId(rs.getLong("id"));
                r.setTexto(rs.getString("texto"));
                r.setECorreta(rs.getBoolean("e_correta"));
                r.setQuestaoId(q.getId());
                return r;
            }, id);

            q.setRespostas(respostas);
        }

        return q;
    }

    public List<Questao> findByTemaId(Long temaId) {
        // Busca apenas os dados da questão, sem as respostas
        String sql = "SELECT * FROM questao WHERE tema_id = ?";
        
        return jdbc.query(sql, (rs, rowNum) -> {
            Questao q = new Questao();
            q.setId(rs.getLong("id"));
            q.setConteudo(rs.getString("conteudo"));
            return q;
        }, temaId);
    }

    @Transactional
    public void save(Questao questao) {
        Long questaoId = questao.getId();

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
            jdbc.update("DELETE FROM resposta WHERE questao_id = ?", questaoId);
        }

        if (questao.getRespostas() != null) {
            for (Resposta r : questao.getRespostas()) {
                jdbc.update("INSERT INTO resposta (texto, e_correta, questao_id) VALUES (?, ?, ?)",
                        r.getTexto(),
                        r.getECorreta(),
                        questaoId);
            }
        }
    }

    // Exclui a questão e suas dependências.
    @Transactional
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM resposta WHERE questao_id = ?", id);
        jdbc.update("DELETE FROM questionario_questao WHERE questao_id = ?", id);
        jdbc.update("DELETE FROM questao WHERE id = ?", id);
    }
}