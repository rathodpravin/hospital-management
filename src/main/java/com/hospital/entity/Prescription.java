package com.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
public class Prescription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "record_id")
	private MedicalRecord medicalRecord;
	@Column(nullable = false)
	private String medicineName;
	private String dosage;
	private String duration;
	private String instructions;
	private LocalDate prescribedDate = LocalDate.now();
}
