package com.hospital.controller;
import com.hospital.dto.ClinicDtos.RecordRequest;
import com.hospital.entity.MedicalRecord;
import com.hospital.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/records") @RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService service;
    @GetMapping public List<MedicalRecord> list() { return service.list(); }
    @GetMapping("/patient/{id}") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')") public List<MedicalRecord> forPatient(@PathVariable Long id) { return service.forPatient(id); }
    @PostMapping @PreAuthorize("hasRole('DOCTOR')") @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecord create(@Valid @RequestBody RecordRequest r) { return service.create(r); }
}
