package com.luisrobbo.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.luisrobbo.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage msg);
	
}
