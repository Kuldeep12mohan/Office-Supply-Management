package com.officesupply.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InventoryItemRequest {
    @NotBlank(message = "Item name is required")
    private String name;

    @Min(value = 0, message = "Quantity must be 0 or greater")
    private int quantity;

    private String description;
}
