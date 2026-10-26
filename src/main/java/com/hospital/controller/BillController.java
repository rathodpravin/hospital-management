package com.hospital.controller;

import com.hospital.dto.ClinicDtos.BillRequest;
import com.hospital.entity.Bill;
import com.hospital.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {
	private final BillService service;

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
	public List<Bill> list() {
		return service.list();
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public Bill create(@Valid @RequestBody BillRequest r) {
		return service.create(r);
	}

	@PatchMapping("/{id}/pay")
	@PreAuthorize("hasRole('ADMIN')")
	public Bill pay(@PathVariable Long id) {
		return service.markPaid(id);
	}
}
