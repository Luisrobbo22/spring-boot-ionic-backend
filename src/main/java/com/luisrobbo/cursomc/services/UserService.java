package com.luisrobbo.cursomc.services;

import org.springframework.security.core.context.SecurityContextHolder;

import com.luisrobbo.cursomc.security.UserSS;

public class UserService {

	// Retorna usuario logado no sistema
	public static UserSS authenticated() {
		try {
			return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			return null;
		}

	}
}
