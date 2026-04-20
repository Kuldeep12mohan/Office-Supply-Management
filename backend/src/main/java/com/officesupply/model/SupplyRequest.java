package com.officesupply.model;

import jakarta.persistence.*;
import lombok.*;
import com.officesupply.enums.RequestStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "supply_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String itemName;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
    
    @ManyToOne
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;
    
    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = true)
    private User approvedBy;
    
    @Column(columnDefinition = "TEXT")
    private String approvalReason;
    
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime approvedAt;
    
    @Column
    private LocalDateTime rejectedAt;
    
    @PreUpdate
    protected void onUpdate() {
        if (status == RequestStatus.APPROVED && approvedAt == null) {
            approvedAt = LocalDateTime.now();
        } else if (status == RequestStatus.REJECTED && rejectedAt == null) {
            rejectedAt = LocalDateTime.now();
        }
    }
}
