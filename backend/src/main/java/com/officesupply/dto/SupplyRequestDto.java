package com.officesupply.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyRequestDto {
    private Long id;
    
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    private String remarks;
    
    private String status;
    private String requestedBy;
    private String approvedBy;
    private String approvalReason;
    private String rejectionReason;
    private String createdAt;
    private String approvedAt;
    private String rejectedAt;
}
