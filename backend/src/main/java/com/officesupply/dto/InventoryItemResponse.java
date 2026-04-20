package com.officesupply.dto;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class InventoryItemResponse {
    private Long id;
    private String name;
    private int quantity;
    private String description;
}
