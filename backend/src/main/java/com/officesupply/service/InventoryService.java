package com.officesupply.service;

import com.officesupply.dto.InventoryItemRequest;
import com.officesupply.dto.InventoryItemResponse;
import com.officesupply.entity.InventoryItem;
import com.officesupply.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    public List<InventoryItemResponse> getAllItems() {
        return inventoryItemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryItemResponse addItem(InventoryItemRequest request) {
        InventoryItem item = InventoryItem.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .build();
        return toResponse(inventoryItemRepository.save(item));
    }

    @Transactional
    public InventoryItemResponse updateItem(Long id, InventoryItemRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));
        item.setName(request.getName());
        item.setQuantity(request.getQuantity());
        item.setDescription(request.getDescription());
        return toResponse(inventoryItemRepository.save(item));
    }

    public InventoryItemResponse toResponse(InventoryItem item) {
        return InventoryItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .description(item.getDescription())
                .build();
    }
}
