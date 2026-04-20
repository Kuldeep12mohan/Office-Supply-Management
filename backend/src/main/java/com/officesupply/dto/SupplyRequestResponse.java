package com.officesupply.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class SupplyRequestResponse {
    private Long id;
    private String employeeUsername;
    private String itemName;
    private int quantity;
    private String remarks;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
