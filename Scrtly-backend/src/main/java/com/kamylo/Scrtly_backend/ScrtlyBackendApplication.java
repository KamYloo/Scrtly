package com.kamylo.Scrtly_backend;

import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ScrtlyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrtlyBackendApplication.class, args);
	}

}
