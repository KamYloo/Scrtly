package com.kamylo.Scrtly_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ScrtlyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrtlyBackendApplication.class, args);
	}

}
