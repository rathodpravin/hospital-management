package com.hospital.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "medical_records") @Getter @Setter @NoArgsConstructor
public class MedicalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "patient_id") private Patient patient;
    @ManyToOne(optional = false) @JoinColumn(name = "doctor_id") private Doctor doctor;
    @Column(nullable = false) private String diagnosis;
    @Column(length = 2000) private String notes;
    private LocalDate recordDate = LocalDate.now();
}
