package com.hospital.service;
import com.hospital.dto.ClinicDtos.RecordRequest;
import com.hospital.entity.*;
import com.hospital.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository repo;
    private final CurrentUser current;
    private final PatientService patientService;
    public MedicalRecord create(RecordRequest r) {
        MedicalRecord m = new MedicalRecord();
        m.setDoctor(current.doctor()); m.setPatient(patientService.get(r.patientId()));
        m.setDiagnosis(r.diagnosis()); m.setNotes(r.notes());
        return repo.save(m);
    }
    public List<MedicalRecord> list() {
        return switch (current.get().getRole()) {
            case ADMIN -> repo.findAll();
            case DOCTOR -> repo.findByDoctorId(current.doctor().getId());
            case PATIENT -> repo.findByPatientId(current.patient().getId());
        };
    }
    public List<MedicalRecord> forPatient(Long patientId) { return repo.findByPatientId(patientId); }
}
