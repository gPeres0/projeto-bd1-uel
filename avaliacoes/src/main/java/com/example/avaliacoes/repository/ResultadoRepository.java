package com.example.avaliacoes.repository;

import com.example.avaliacoes.model.Resultado;
import com.example.avaliacoes.dto.EstatisticaTemaDTO;
import com.example.avaliacoes.dto.GraficoPontoDTO;
import com.example.avaliacoes.model.Questionario;
import com.example.avaliacoes.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ResultadoRepository {

    private final JdbcTemplate jdbc;

    public ResultadoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapper que preenche o Resultado e os objetos aninhados (Usuario e Questionario)
    private RowMapper<Resultado> resultadoMapper = (rs, rowNum) -> {
        Resultado r = new Resultado();
        r.setId(rs.getLong("id"));
        r.setNota(rs.getDouble("nota"));
        
        r.setData(rs.getObject("data", LocalDateTime.class));

        // Monta objeto Usuario (N:1)
        Usuario u = new Usuario();
        u.setId(rs.getLong("id_user"));
        // Verifica se a coluna do JOIN existe antes de tentar pegar o nome
        try {
            u.setNome(rs.getString("usuario_nome")); 
        } catch (Exception e) { /* Coluna não existe no result set, ignora */ }
        r.setUsuario(u);

        // Monta objeto Questionario (N:1)
        Questionario q = new Questionario();
        q.setId(rs.getLong("id_questionario"));
        try {
            q.setNome(rs.getString("questionario_nome"));
        } catch (Exception e) {}
        r.setQuestionario(q);

        return r;
    };

    
    // Busca todos os resultados, trazendo nomes de Usuário e Questionário via JOIN.
    public List<Resultado> findAll() {
        String sql = """
            SELECT 
                r.*, 
                u.nome as usuario_nome, 
                q.nome as questionario_nome 
            FROM resultado r
            JOIN usuario u ON r.id_user = u.id
            JOIN questionario q ON r.id_questionario = q.id
            ORDER BY r.data DESC
        """;
        return jdbc.query(sql, resultadoMapper);
    }

    // Busca resultados específicos de um usuário (Para histórico pessoal)
    public List<Resultado> findByUsuarioId(Long userId) {
        String sql = """
            SELECT 
                r.*, 
                u.nome as usuario_nome, 
                q.nome as questionario_nome 
            FROM resultado r
            JOIN usuario u ON r.id_user = u.id
            JOIN questionario q ON r.id_questionario = q.id
            WHERE r.id_user = ?
            ORDER BY r.data DESC
        """;
        return jdbc.query(sql, resultadoMapper, userId);
    }

    // Salva o resultado.
    public Resultado save(Resultado r) {
        if (r.getId() == null) {
            String sql = "INSERT INTO resultado (nota, data, id_user, id_questionario) VALUES (?, ?, ?, ?) RETURNING id";
            
            // Define a data atual se não vier preenchida
            if (r.getData() == null) r.setData(LocalDateTime.now());

            Long novoId = jdbc.queryForObject(sql, Long.class,
                r.getNota(),
                r.getData(),
                r.getUsuario().getId(),
                r.getQuestionario().getId()
            );
            r.setId(novoId);
        } else {
            String sql = "UPDATE resultado SET nota = ?, data = ?, id_user = ?, id_questionario = ? WHERE id = ?";
            jdbc.update(sql,
                r.getNota(),
                r.getData(),
                r.getUsuario().getId(),
                r.getQuestionario().getId(),
                r.getId()
            );
        }
        return r;
    }

    // Busca dados para o gráfico de linha (tema, data e nota).
    public List<GraficoPontoDTO> findHistoricoGrafico(Long userId) {
        String sql = """
            SELECT t.nome as tema, to_char(r.data, 'DD/MM HH24:MI') as data_fmt, r.nota
            FROM resultado r
            JOIN questionario q ON r.id_questionario = q.id
            JOIN tema t ON q.tema_id = t.id
            WHERE r.id_user = ?
            ORDER BY r.data ASC
        """;
        
        return jdbc.query(sql, (rs, rowNum) -> new GraficoPontoDTO(
            rs.getString("tema"),
            rs.getString("data_fmt"),
            rs.getDouble("nota")
        ), userId);
    }

    // Calcula estatísticas agrupadas por tema.
    public List<EstatisticaTemaDTO> findEstatisticasPorTema(Long userId) {
        String sql = """
            SELECT 
                t.nome as tema, 
                AVG(r.nota) as media, 
                COUNT(r.id) as total_q
            FROM resultado r
            JOIN questionario q ON r.id_questionario = q.id
            JOIN tema t ON q.tema_id = t.id
            WHERE r.id_user = ?
            GROUP BY t.nome
            ORDER BY media DESC
        """;

        return jdbc.query(sql, (rs, rowNum) -> new EstatisticaTemaDTO(
            rs.getString("tema"),
            Math.round(rs.getDouble("media") * 10.0) / 10.0,
            rs.getInt("total_q")
        ), userId);
    }
}