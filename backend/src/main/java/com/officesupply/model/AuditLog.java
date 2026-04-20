package com.officesupply.model;

import jakarta.persistence.*;
import lombok.*;
import com.officesupply.enums.AuditAction;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;
    
    @ManyToOne
    @JoinColumn(name = "action_by", nullable = false)
    private User actionBy;
    
    @Column
    private Long itemId;
    
    @Column(columnDefinition = "LONGTEXT")
    private String details;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
