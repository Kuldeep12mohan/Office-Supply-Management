package com.officesupply.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {
    private Long id;
    
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "Reorder level is required")
    @Positive(message = "Reorder level must be positive")
    private Integer reorderLevel;
    
    private String lastUpdatedBy;
    private String updatedAt;
    private String createdAt;
}
