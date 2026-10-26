package com.hospital.service;
import com.hospital.entity.*;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
@Service @RequiredArgsConstructor
public class DashboardService {
    private final CurrentUser current;
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final MedicalRecordRepository records;
    private final BillRepository bills;

    public Map<String, Object> get() {
        Map<String, Object> m = new LinkedHashMap<>();
        switch (current.get().getRole()) {
            case ADMIN -> {
                m.put("totalDoctors", doctors.count());
                m.put("totalPatients", patients.count());
                m.put("totalAppointments", appointments.count());
                m.put("scheduledAppointments", appointments.countByStatus(AppointmentStatus.SCHEDULED));
                m.put("revenueCollected", bills.sumByStatus(BillStatus.PAID));
                m.put("pendingPayments", bills.sumByStatus(BillStatus.UNPAID));
            }
            case DOCTOR -> {
                Long id = current.doctor().getId();
                LocalDate today = LocalDate.now();
                m.put("appointmentsToday", appointments.countByDoctorIdAndDateTimeBetween(id, today.atStartOfDay(), today.plusDays(1).atStartOfDay()));
                m.put("totalAppointments", appointments.findByDoctorIdOrderByDateTimeDesc(id).size());
                m.put("recordsWritten", records.findByDoctorId(id).size());
            }
            case PATIENT -> {
                Long id = current.patient().getId();
                m.put("upcomingAppointments", appointments.countByPatientIdAndStatusAndDateTimeAfter(id, AppointmentStatus.SCHEDULED, LocalDateTime.now()));
                m.put("medicalRecords", records.findByPatientId(id).size());
                m.put("unpaidBills", bills.sumByStatusAndPatient(BillStatus.UNPAID, id));
            }
        }
        return m;
    }
}
