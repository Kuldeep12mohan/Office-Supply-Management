package com.officesupply.repository;

import com.officesupply.model.AuditLog;
import com.officesupply.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action);
    List<AuditLog> findAllByOrderByCreatedAtDesc();
}
