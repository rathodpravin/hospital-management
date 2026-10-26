package com.hospital.repository;
import com.hospital.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPatientId(Long patientId);
    @Query("select coalesce(sum(b.amount),0) from Bill b where b.status = :status")
    BigDecimal sumByStatus(@Param("status") BillStatus status);
    @Query("select coalesce(sum(b.amount),0) from Bill b where b.status = :status and b.patient.id = :pid")
    BigDecimal sumByStatusAndPatient(@Param("status") BillStatus status, @Param("pid") Long pid);
}
