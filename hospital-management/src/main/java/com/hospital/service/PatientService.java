package com.hospital.service;
import com.hospital.dto.ClinicDtos.PatientRequest;
import com.hospital.entity.Patient;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repo;
    private final CurrentUser current;
    public List<Patient> all() { return repo.findAll(); }
    public Patient get(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id)); }
    public Patient me() { return current.patient(); }
    public Patient update(Long id, PatientRequest r) { return save(get(id), r); }
    public Patient updateMe(PatientRequest r) { return save(current.patient(), r); }
    public void delete(Long id) { repo.delete(get(id)); }
    private Patient save(Patient p, PatientRequest r) {
        p.setName(r.name()); p.setDateOfBirth(r.dateOfBirth()); p.setGender(r.gender());
        p.setPhone(r.phone()); p.setAddress(r.address()); p.setBloodGroup(r.bloodGroup());
        return repo.save(p);
    }
}
