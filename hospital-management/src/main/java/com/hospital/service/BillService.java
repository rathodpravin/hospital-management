package com.hospital.service;
import com.hospital.dto.ClinicDtos.BillRequest;
import com.hospital.entity.*;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class BillService {
    private final BillRepository repo;
    private final AppointmentRepository appointments;
    private final PatientService patientService;
    private final CurrentUser current;
    public Bill create(BillRequest r) {
        Bill b = new Bill();
        b.setPatient(patientService.get(r.patientId()));
        if (r.appointmentId() != null)
            b.setAppointment(appointments.findById(r.appointmentId()).orElseThrow(() -> new ResourceNotFoundException("Appointment not found")));
        b.setDescription(r.description()); b.setAmount(r.amount());
        return repo.save(b);
    }
    public List<Bill> list() {
        return current.get().getRole() == Role.ADMIN ? repo.findAll() : repo.findByPatientId(current.patient().getId());
    }
    public Bill markPaid(Long id) {
        Bill b = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + id));
        b.setStatus(BillStatus.PAID);
        return repo.save(b);
    }
}
