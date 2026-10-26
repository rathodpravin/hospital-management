package com.hospital.controller;

import com.hospital.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
	private final DashboardService service;

	@GetMapping
	public Map<String, Object> get() {
		return service.get();
	}
}
