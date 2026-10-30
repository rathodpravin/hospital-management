package com.hospital.service;
import com.hospital.entity.*;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
/** Helper: who is logged in right now? */
@Component @RequiredArgsConstructor
public class CurrentUser {
    private final UserRepository users;
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    public User get() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByUsername(name).orElseThrow(() -> new AccessDeniedException("Unknown user"));
    }
    public Doctor doctor() { return doctors.findByUserId(get().getId()).orElseThrow(() -> new AccessDeniedException("Not a doctor")); }
    public Patient patient() { return patients.findByUserId(get().getId()).orElseThrow(() -> new AccessDeniedException("Not a patient")); }
}
