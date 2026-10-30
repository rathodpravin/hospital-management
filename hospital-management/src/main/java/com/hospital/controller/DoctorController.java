package com.hospital.controller;
import com.hospital.dto.ClinicDtos.DoctorRequest;
import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/doctors") @RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;
    @GetMapping public List<Doctor> all() { return service.all(); }
    @GetMapping("/{id}") public Doctor one(@PathVariable Long id) { return service.get(id); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.CREATED)
    public Doctor create(@Valid @RequestBody DoctorRequest r) { return service.create(r); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public Doctor update(@PathVariable Long id, @Valid @RequestBody DoctorRequest r) { return service.update(id, r); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
