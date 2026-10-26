package com.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
public class Bill {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_id")
	private Patient patient;
	@ManyToOne
	@JoinColumn(name = "appointment_id")
	private Appointment appointment;
	private String description;
	@Column(nullable = false)
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
	private BillStatus status = BillStatus.UNPAID;
	private LocalDate billDate = LocalDate.now();
}
