package com.hospital.service;
import com.hospital.dto.ClinicDtos.PrescriptionRequest;
import com.hospital.entity.*;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class PrescriptionService {
    private final PrescriptionRepository repo;
    private final MedicalRecordRepository records;
    private final CurrentUser current;
    public Prescription create(PrescriptionRequest r) {
        MedicalRecord rec = records.findById(r.recordId()).orElseThrow(() -> new ResourceNotFoundException("Medical record not found"));
        if (!rec.getDoctor().getId().equals(current.doctor().getId())) throw new AccessDeniedException("Not your record");
        Prescription p = new Prescription();
        p.setMedicalRecord(rec); p.setMedicineName(r.medicineName()); p.setDosage(r.dosage());
        p.setDuration(r.duration()); p.setInstructions(r.instructions());
        return repo.save(p);
    }
    public List<Prescription> list() {
        return switch (current.get().getRole()) {
            case ADMIN -> repo.findAll();
            case DOCTOR -> repo.findByMedicalRecordDoctorId(current.doctor().getId());
            case PATIENT -> repo.findByMedicalRecordPatientId(current.patient().getId());
        };
    }
}
