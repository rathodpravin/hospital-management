package com.hospital.controller;

import com.hospital.dto.AuthDtos.*;
import com.hospital.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService service;

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest r) {
		return service.login(r);
	}

	@PostMapping("/register")
	public AuthResponse register(@Valid @RequestBody RegisterRequest r) {
		return service.register(r);
	}
}
