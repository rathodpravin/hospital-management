package com.hospital.service;
import com.hospital.dto.ClinicDtos.AppointmentRequest;
import com.hospital.entity.*;
import com.hospital.exception.*;
import com.hospital.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository repo;
    private final CurrentUser current;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Transactional
    public Appointment book(AppointmentRequest r) {
        Patient patient;
        if (current.get().getRole() == Role.PATIENT) patient = current.patient();
        else {
            if (r.patientId() == null) throw new BadRequestException("patientId is required");
            patient = patientService.get(r.patientId());
        }
        Doctor doctor = doctorService.get(r.doctorId());
        if (repo.existsByDoctorIdAndDateTimeAndStatusNot(doctor.getId(), r.dateTime(), AppointmentStatus.CANCELLED))
            throw new BadRequestException("Doctor is already booked at that time");
        Appointment a = new Appointment();
        a.setPatient(patient); a.setDoctor(doctor); a.setDateTime(r.dateTime()); a.setReason(r.reason());
        return repo.save(a);
    }
    /** Admin sees all, doctor sees own, patient sees own. */
    public List<Appointment> list() {
        return switch (current.get().getRole()) {
            case ADMIN -> repo.findAllByOrderByDateTimeDesc();
            case DOCTOR -> repo.findByDoctorIdOrderByDateTimeDesc(current.doctor().getId());
            case PATIENT -> repo.findByPatientIdOrderByDateTimeDesc(current.patient().getId());
        };
    }
    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus status) {
        Appointment a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
        switch (current.get().getRole()) {
            case DOCTOR -> { if (!a.getDoctor().getId().equals(current.doctor().getId())) throw new AccessDeniedException("Not your appointment"); }
            case PATIENT -> {
                if (!a.getPatient().getId().equals(current.patient().getId())) throw new AccessDeniedException("Not your appointment");
                if (status != AppointmentStatus.CANCELLED) throw new AccessDeniedException("Patients can only cancel");
            }
            default -> { }
        }
        a.setStatus(status);
        return repo.save(a);
    }
}
