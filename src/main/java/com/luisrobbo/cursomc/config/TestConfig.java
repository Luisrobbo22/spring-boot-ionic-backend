package com.luisrobbo.cursomc.config;

import com.luisrobbo.cursomc.services.DBService;
import com.luisrobbo.cursomc.services.EmailService;
import com.luisrobbo.cursomc.services.MockEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.text.ParseException;

@Configuration
@Profile("test")
public class TestConfig {

	@Autowired
	private DBService dbService;

	@Bean
	public boolean initantiateDatabase() throws ParseException {
		dbService.instatiateDatabase();
		return true;
	}

	@Bean
	public EmailService emailService() {
		return new MockEmailService();
	}
}
