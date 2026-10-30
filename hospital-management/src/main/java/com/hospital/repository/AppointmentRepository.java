package com.hospital.repository;
import com.hospital.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByDateTimeDesc();
    List<Appointment> findByPatientIdOrderByDateTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByDateTimeDesc(Long doctorId);
    boolean existsByDoctorIdAndDateTimeAndStatusNot(Long doctorId, LocalDateTime dateTime, AppointmentStatus status);
    long countByStatus(AppointmentStatus status);
    long countByDoctorIdAndDateTimeBetween(Long doctorId, LocalDateTime from, LocalDateTime to);
    long countByPatientIdAndStatusAndDateTimeAfter(Long patientId, AppointmentStatus status, LocalDateTime after);
}
