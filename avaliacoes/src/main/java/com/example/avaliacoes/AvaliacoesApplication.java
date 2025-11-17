package com.example.avaliacoes;

import com.example.avaliacoes.model.Tema;
import com.example.avaliacoes.repository.TemaRepository;
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
            // Verifica se o tema 'Geral' já existe para evitar duplicidade
            if (temaRepository.count() == 0) {
                System.out.println("Criando Tema inicial...");
                
                Tema tema = new Tema();
                // O nome do Tema é o único atributo simples que definimos na Entidade.
                tema.setNome("Geral"); 
                
                temaRepository.save(tema);
                System.out.println("Tema 'Geral' salvo com ID: " + tema.getId());
            }
        };
    }
}