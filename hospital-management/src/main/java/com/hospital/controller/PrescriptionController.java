package com.hospital.controller;
import com.hospital.dto.ClinicDtos.PrescriptionRequest;
import com.hospital.entity.Prescription;
import com.hospital.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/prescriptions") @RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService service;
    @GetMapping public List<Prescription> list() { return service.list(); }
    @PostMapping @PreAuthorize("hasRole('DOCTOR')") @ResponseStatus(HttpStatus.CREATED)
    public Prescription create(@Valid @RequestBody PrescriptionRequest r) { return service.create(r); }
}
