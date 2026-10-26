package com.hospital.dto;
import com.hospital.entity.AppointmentStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class ClinicDtos {
    public record DoctorRequest(@NotBlank String name, @NotBlank String specialization,
        @Pattern(regexp = "^[0-9+\\-]{7,15}$", message = "Invalid phone number") String phone,
        @Email String email, @NotNull @PositiveOrZero BigDecimal consultationFee,
        String username, String password) {}
    public record PatientRequest(@NotBlank String name, @Past LocalDate dateOfBirth, String gender,
        @Pattern(regexp = "^[0-9+\\-]{7,15}$", message = "Invalid phone number") String phone,
        String address, String bloodGroup) {}
    public record AppointmentRequest(Long patientId, @NotNull Long doctorId,
        @NotNull @Future LocalDateTime dateTime, String reason) {}
    public record StatusRequest(@NotNull AppointmentStatus status) {}
    public record RecordRequest(@NotNull Long patientId, @NotBlank String diagnosis, String notes) {}
    public record PrescriptionRequest(@NotNull Long recordId, @NotBlank String medicineName,
        @NotBlank String dosage, String duration, String instructions) {}
    public record BillRequest(@NotNull Long patientId, Long appointmentId,
        @NotBlank String description, @NotNull @Positive BigDecimal amount) {}
}
