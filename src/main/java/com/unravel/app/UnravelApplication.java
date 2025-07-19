package com.unravel.app;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.unravel.app.manager.SessionManager;

@SpringBootApplication
public class UnravelApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnravelApplication.class, args);
	}
	
	@Bean
    public SessionManager sessionManager() {
        return new SessionManager("redis://localhost:6379/0");
    }

}
