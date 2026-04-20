package com.officesupply.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.officesupply.model.Inventory;
import com.officesupply.model.User;
import com.officesupply.exception.ResourceNotFoundException;
import com.officesupply.repository.InventoryRepository;
import com.officesupply.dto.InventoryDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public Inventory createInventory(String itemName, Integer quantity, Integer reorderLevel, Long userId) {
        log.info("Creating inventory item: {}", itemName);
        
        User user = userService.getUserById(userId);
        
        Inventory inventory = Inventory.builder()
                .itemName(itemName)
                .quantity(quantity)
                .reorderLevel(reorderLevel)
                .lastUpdatedBy(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Inventory saved = inventoryRepository.save(inventory);
        auditLogService.logInventoryCreated(saved.getId(), saved.getItemName(), saved.getQuantity());
        
        return saved;
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));
    }

    public Inventory getInventoryByItemName(String itemName) {
        return inventoryRepository.findByItemName(itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + itemName));
    }

    public List<InventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Inventory updateInventory(Long id, Integer quantity, Integer reorderLevel, Long userId) {
        log.info("Updating inventory item: {}", id);
        
        Inventory inventory = getInventoryById(id);
        User user = userService.getUserById(userId);
        
        inventory.setQuantity(quantity);
        inventory.setReorderLevel(reorderLevel);
        inventory.setLastUpdatedBy(user);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        Inventory updated = inventoryRepository.save(inventory);
        auditLogService.logInventoryUpdated(updated.getId(), updated.getItemName(), updated.getQuantity());
        
        return updated;
    }

    public List<InventoryDto> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getQuantity() <= inv.getReorderLevel())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private InventoryDto convertToDto(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())
                .itemName(inventory.getItemName())
                .quantity(inventory.getQuantity())
                .reorderLevel(inventory.getReorderLevel())
                .lastUpdatedBy(inventory.getLastUpdatedBy() != null ? inventory.getLastUpdatedBy().getFullName() : "SYSTEM")
                .updatedAt(inventory.getUpdatedAt().toString())
                .createdAt(inventory.getCreatedAt().toString())
                .build();
    }
}
