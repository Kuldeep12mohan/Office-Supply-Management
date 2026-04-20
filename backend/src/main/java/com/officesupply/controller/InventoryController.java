package com.officesupply.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.officesupply.dto.InventoryDto;
import com.officesupply.dto.ApiResponse;
import com.officesupply.service.InventoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createInventory(
            @Valid @RequestBody InventoryDto inventoryDto,
            HttpSession session) {
        log.info("Creating inventory item: {}", inventoryDto.getItemName());
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var inventory = inventoryService.createInventory(
                inventoryDto.getItemName(),
                inventoryDto.getQuantity(),
                inventoryDto.getReorderLevel(),
                userId
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .success(true)
                        .message("Inventory item created successfully")
                        .data(inventory)
                        .timestamp(LocalDateTime.now().toString())
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllInventory() {
        List<InventoryDto> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("All inventory items retrieved")
                .data(inventory)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getInventoryById(@PathVariable Long id) {
        var inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Inventory item retrieved")
                .data(inventory)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryDto inventoryDto,
            HttpSession session) {
        log.info("Updating inventory item: {}", id);
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var updated = inventoryService.updateInventory(
                id,
                inventoryDto.getQuantity(),
                inventoryDto.getReorderLevel(),
                userId
        );

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Inventory item updated successfully")
                .data(updated)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<?>> getLowStockItems() {
        List<InventoryDto> lowStockItems = inventoryService.getLowStockItems();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Low stock items retrieved")
                .data(lowStockItems)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
