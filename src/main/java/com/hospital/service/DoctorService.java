package com.hospital.service;
import com.hospital.dto.ClinicDtos.DoctorRequest;
import com.hospital.entity.*;
import com.hospital.exception.*;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository repo;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public List<Doctor> all() { return repo.findAll(); }
    public Doctor get(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id)); }
    @Transactional
    public Doctor create(DoctorRequest r) {
        if (r.username() == null || r.username().length() < 3 || r.password() == null || r.password().length() < 6)
            throw new BadRequestException("Login username (3+ chars) and password (6+ chars) are required");
        if (users.existsByUsername(r.username())) throw new BadRequestException("Username already taken");
        User u = new User();
        u.setUsername(r.username()); u.setPassword(encoder.encode(r.password())); u.setRole(Role.DOCTOR);
        Doctor d = new Doctor(); d.setUser(u);
        return repo.save(apply(d, r));
    }
    public Doctor update(Long id, DoctorRequest r) { return repo.save(apply(get(id), r)); }
    public void delete(Long id) { repo.delete(get(id)); }
    private Doctor apply(Doctor d, DoctorRequest r) {
        d.setName(r.name()); d.setSpecialization(r.specialization()); d.setPhone(r.phone());
        d.setEmail(r.email()); d.setConsultationFee(r.consultationFee());
        return d;
    }
}
