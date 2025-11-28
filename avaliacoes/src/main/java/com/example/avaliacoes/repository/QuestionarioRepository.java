package com.example.avaliacoes.repository;

import com.example.avaliacoes.model.Questionario;
import com.example.avaliacoes.model.Resposta;
import com.example.avaliacoes.dto.QuestionarioExibicaoDTO;
import com.example.avaliacoes.model.Questao;
import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        Usuario u = new Usuario();
        u.setId(rs.getLong("id_user"));
        q.setUsuario(u);

        Tema t = new Tema();
        t.setId(rs.getLong("tema_id"));
        try {
            t.setNome(rs.getString("tema_nome"));
        } catch (Exception e) {}
        q.setTema(t);

        return q;
    };

    // Busca todos os questionários.
    public List<Questionario> findAll() {
        String sql = """
            SELECT q.*, t.nome as tema_nome 
            FROM questionario q 
            LEFT JOIN tema t ON q.tema_id = t.id
        """;
        List<Questionario> lista = jdbc.query(sql, questionarioMapper);

        for (Questionario q : lista) {
            carregarQuestoes(q);
        }
        
        return lista;
    }

    public Questionario findById(Long id) {
        String sql = """
            SELECT q.*, t.nome as tema_nome 
            FROM questionario q 
            LEFT JOIN tema t ON q.tema_id = t.id 
            WHERE q.id = ?
        """;
        Questionario q = jdbc.queryForObject(sql, questionarioMapper, id);
        if (q != null) {
            carregarQuestoes(q);
        }
        return q;
    }

    private void carregarQuestoes(Questionario q) {
        String sqlQuestoes = """
            SELECT quest.* FROM questao quest 
            JOIN questionario_questao qq ON quest.id = qq.questao_id 
            WHERE qq.questionario_id = ?
        """;
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

        // Inserir ou Atualizar tabela 'questionario'
        if (questionarioId == null) {
            String sql = "INSERT INTO questionario (nome, id_user, tema_id) VALUES (?, ?, ?) RETURNING id";
            questionarioId = jdbc.queryForObject(sql, Long.class, 
                q.getNome(), 
                q.getUsuario() != null ? q.getUsuario().getId() : 0,
                q.getTema() != null ? q.getTema().getId() : 0
            );
            q.setId(questionarioId);
        } else {
            jdbc.update("UPDATE questionario SET nome = ?, id_user = ? WHERE id = ?",
                q.getNome(), 
                q.getUsuario() != null ? q.getUsuario().getId() : 0,
                q.getTema() != null ? q.getTema().getId() : 0,
                questionarioId);
            
            // Limpar relacionamentos antigos para recriar
            jdbc.update("DELETE FROM questionario_questao WHERE questionario_id = ?", questionarioId);
        }

        // Salvar relacionamentos na tabela 'questionario_questao'
        if (q.getQuestoes() != null) {
            for (Questao k : q.getQuestoes()) {
                jdbc.update("INSERT INTO questionario_questao (questionario_id, questao_id) VALUES (?, ?)",
                    questionarioId, k.getId());
            }
        }
    }

    // Busca questionários e junta com o resultado do usuário específico (se existir).
    public List<QuestionarioExibicaoDTO> findAllWithStatusForUser(Long userId) {
        String sql = """
            SELECT DISTINCT ON (q.id)
                q.id,
                q.nome AS titulo,
                t.nome AS tema_nome,
                r.nota,
                r.data AS data_resolucao
            FROM questionario q
            LEFT JOIN tema t ON q.tema_id = t.id
            LEFT JOIN resultado r ON q.id = r.id_questionario AND r.id_user = ?
            ORDER BY q.id, r.data DESC
        """;

        return jdbc.query(sql, (rs, rowNum) -> new QuestionarioExibicaoDTO(
            rs.getLong("id"),
            rs.getString("titulo"),
            rs.getString("tema_nome"),
            rs.getObject("nota", Double.class), 
            rs.getObject("data_resolucao", LocalDateTime.class)
        ), userId);
    }

    // Método principal para buscar o questionário completo
    public Questionario findByIdCompleto(Long id) {
        String sql = "SELECT * FROM questionario WHERE id = ?";
        Questionario q = jdbc.queryForObject(sql, (rs, rn) -> {
            Questionario obj = new Questionario();
            obj.setId(rs.getLong("id"));
            obj.setNome(rs.getString("nome"));
            return obj;
        }, id);

        if (q != null) {
            carregarQuestoesComRespostas(q);
        }
        return q;
    }

    private void carregarQuestoesComRespostas(Questionario q) {
        String sql = """
            SELECT quest.* FROM questao quest 
            JOIN questionario_questao qq ON quest.id = qq.questao_id 
            WHERE qq.questionario_id = ?
        """;
        
        List<Questao> questoes = jdbc.query(sql, (rs, rowNum) -> {
            Questao k = new Questao();
            k.setId(rs.getLong("id"));
            k.setConteudo(rs.getString("conteudo"));
            return k;
        }, q.getId());

        for (Questao questao : questoes) {
            String sqlResp = "SELECT * FROM resposta WHERE questao_id = ?";
            List<Resposta> respostas = jdbc.query(sqlResp, (rs, rn) -> {
                Resposta r = new Resposta();
                r.setId(rs.getLong("id"));
                r.setTexto(rs.getString("texto"));
                r.setECorreta(rs.getBoolean("e_correta"));
                return r;
            }, questao.getId());
            
            questao.setRespostas(respostas);
        }
        q.setQuestoes(questoes);
    }
}