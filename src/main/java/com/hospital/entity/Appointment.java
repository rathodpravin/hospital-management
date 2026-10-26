package com.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_id")
	private Patient patient;
	@ManyToOne(optional = false)
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;
	@Column(nullable = false)
	private LocalDateTime dateTime;
	private String reason;
	@Enumerated(EnumType.STRING)
	private AppointmentStatus status = AppointmentStatus.SCHEDULED;
}
