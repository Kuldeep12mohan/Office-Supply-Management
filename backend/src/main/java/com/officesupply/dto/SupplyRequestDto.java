package com.officesupply.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SupplyRequestDto {
    @NotBlank(message = "Item name is required")
    private String itemName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String remarks;
}
