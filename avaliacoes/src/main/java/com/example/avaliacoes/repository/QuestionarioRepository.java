package com.example.avaliacoes.repository;

import com.example.avaliacoes.model.Questionario;
import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionarioRepository {

    private final JdbcTemplate jdbc;
    // private final TemaRepository temaRepository; 

    public QuestionarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Questionario> questionarioMapper = (rs, rowNum) -> {
        Questionario q = new Questionario();
        q.setId(rs.getLong("id"));
        q.setNome(rs.getString("nome"));
        q.setNota(rs.getObject("nota", Integer.class));

        // Reconstrói o objeto Usuario apenas com o ID (Lazy load manual)
        Usuario u = new Usuario();
        u.setId(rs.getLong("id_user"));
        q.setUsuario(u);

        return q;
    };

    // Busca todos os questionários.
    public List<Questionario> findAll() {
        String sql = "SELECT * FROM questionario";
        List<Questionario> lista = jdbc.query(sql, questionarioMapper);

        // Popula as listas de Temas e Questões para cada Questionário encontrado
        for (Questionario q : lista) {
            carregarRelacionamentos(q);
        }
        
        return lista;
    }

    public Questionario findById(Long id) {
        String sql = "SELECT * FROM questionario WHERE id = ?";
        Questionario q = jdbc.queryForObject(sql, questionarioMapper, id);
        if (q != null) {
            carregarRelacionamentos(q);
        }
        return q;
    }

    // Método auxiliar para buscar as listas N:M
    private void carregarRelacionamentos(Questionario q) {
        // 1. Buscar Temas
        String sqlTemas = "SELECT t.* FROM tema t " +
                          "JOIN questionario_tema qt ON t.id = qt.tema_id " +
                          "WHERE qt.questionario_id = ?";
        List<Tema> temas = jdbc.query(sqlTemas, (rs, rowNum) -> 
            new Tema(rs.getLong("id"), rs.getString("nome")), q.getId());
        q.setTemas(temas);

        // 2. Buscar Questões
        String sqlQuestoes = "SELECT quest.* FROM questao quest " +
                             "JOIN questionario_questao qq ON quest.id = qq.questao_id " +
                             "WHERE qq.questionario_id = ?";
        List<Questao> questoes = jdbc.query(sqlQuestoes, (rs, rowNum) -> {
            Questao k = new Questao();
            k.setId(rs.getLong("id"));
            k.setConteudo(rs.getString("conteudo"));
            return k;
        }, q.getId());
        q.setQuestoes(questoes);
    }

    @Transactional
    public void save(Questionario q) {
        Long questionarioId = q.getId();

        // 1. Inserir ou Atualizar tabela 'questionario'
        if (questionarioId == null) {
            String sql = "INSERT INTO questionario (nome, nota, id_user) VALUES (?, ?, ?) RETURNING id";
            questionarioId = jdbc.queryForObject(sql, Long.class, 
                q.getNome(), 
                q.getNota(), 
                q.getUsuario() != null ? q.getUsuario().getId() : null);
            q.setId(questionarioId);
        } else {
            jdbc.update("UPDATE questionario SET nome = ?, nota = ?, id_user = ? WHERE id = ?",
                q.getNome(), 
                q.getNota(), 
                q.getUsuario() != null ? q.getUsuario().getId() : null,
                questionarioId);
            
            // Limpar relacionamentos antigos para recriar
            jdbc.update("DELETE FROM questionario_tema WHERE questionario_id = ?", questionarioId);
            jdbc.update("DELETE FROM questionario_questao WHERE questionario_id = ?", questionarioId);
        }

        // 2. Salvar relacionamentos na tabela 'questionario_tema'
        if (q.getTemas() != null) {
            for (Tema t : q.getTemas()) {
                jdbc.update("INSERT INTO questionario_tema (questionario_id, tema_id) VALUES (?, ?)",
                    questionarioId, t.getId());
            }
        }

        // 3. Salvar relacionamentos na tabela 'questionario_questao'
        if (q.getQuestoes() != null) {
            for (Questao k : q.getQuestoes()) {
                jdbc.update("INSERT INTO questionario_questao (questionario_id, questao_id) VALUES (?, ?)",
                    questionarioId, k.getId());
            }
        }
    }
}