package com.example.avaliacoes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // ready for use by Spring MVC to handle web requests
public class HelloController {
    
    @GetMapping("/") // maps / to the index() method
	public String index() {
		return "Greetings from Spring Boot!";
	}

}
