package com.hospital.service;
import com.hospital.dto.AuthDtos.*;
import com.hospital.entity.*;
import com.hospital.exception.BadRequestException;
import com.hospital.repository.*;
import com.hospital.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserRepository users;
    private final PatientRepository patients;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthResponse login(LoginRequest r) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(r.username(), r.password()));
        User u = users.findByUsername(r.username()).orElseThrow();
        return new AuthResponse(jwt.generate(u.getUsername(), u.getRole().name()), u.getRole().name(), u.getUsername());
    }
    /** Public self-registration: always creates a PATIENT. */
    @Transactional
    public AuthResponse register(RegisterRequest r) {
        if (users.existsByUsername(r.username())) throw new BadRequestException("Username already taken");
        User u = new User();
        u.setUsername(r.username()); u.setPassword(encoder.encode(r.password())); u.setRole(Role.PATIENT);
        Patient p = new Patient();
        p.setUser(u); p.setName(r.name()); p.setDateOfBirth(r.dateOfBirth()); p.setGender(r.gender());
        p.setPhone(r.phone()); p.setAddress(r.address()); p.setBloodGroup(r.bloodGroup());
        patients.save(p);
        return new AuthResponse(jwt.generate(u.getUsername(), "PATIENT"), "PATIENT", u.getUsername());
    }
}
