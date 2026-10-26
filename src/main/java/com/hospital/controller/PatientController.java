package com.hospital.controller;

import com.hospital.dto.ClinicDtos.PatientRequest;
import com.hospital.entity.Patient;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
	private final PatientService service;

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	public List<Patient> all() {
		return service.all();
	}

	@GetMapping("/me")
	@PreAuthorize("hasRole('PATIENT')")
	public Patient me() {
		return service.me();
	}

	@PutMapping("/me")
	@PreAuthorize("hasRole('PATIENT')")
	public Patient updateMe(@Valid @RequestBody PatientRequest r) {
		return service.updateMe(r);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
	public Patient one(@PathVariable Long id) {
		return service.get(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public Patient update(@PathVariable Long id, @Valid @RequestBody PatientRequest r) {
		return service.update(id, r);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}
}
