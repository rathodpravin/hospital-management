package com.hospital.controller;
import com.hospital.dto.ClinicDtos.*;
import com.hospital.entity.Appointment;
import com.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/appointments") @RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService service;
    @GetMapping public List<Appointment> list() { return service.list(); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','PATIENT')") @ResponseStatus(HttpStatus.CREATED)
    public Appointment book(@Valid @RequestBody AppointmentRequest r) { return service.book(r); }
    @PatchMapping("/{id}/status")
    public Appointment status(@PathVariable Long id, @Valid @RequestBody StatusRequest r) { return service.updateStatus(id, r.status()); }
}
