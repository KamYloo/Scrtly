package com.kamylo.Scrtly_backend;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableRabbit
public class ScrtlyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrtlyBackendApplication.class, args);
	}

}
