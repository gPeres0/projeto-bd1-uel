package com.example.avaliacoes;

// import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.repository.TemaRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AvaliacoesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvaliacoesApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializationRunner(TemaRepository temaRepository) {
        return args -> {
            String url = "jdbc:postgresql://localhost:5432/avaliacoes_db";
            String user = "postgres";
            String password = "123456";
                    
            try {
                // Optional: for older drivers, you might need Class.forName("org.postgresql.Driver"); 
                // Modern JDBC 4.0+ drivers load automatically.
                Connection con = DriverManager.getConnection(url, user, password);
                // Connection successful, proceed with database operations...
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle connection errors (e.g., wrong port, refused connection)
            }
        };
    }
}