package com.officesupply;

import com.officesupply.dto.InventoryItemRequest;
import com.officesupply.dto.InventoryItemResponse;
import com.officesupply.entity.InventoryItem;
import com.officesupply.repository.InventoryItemRepository;
import com.officesupply.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryItemRepository inventoryItemRepository;
    @InjectMocks private InventoryService inventoryService;

    @Test
    void getAllItems_ReturnsAllItems() {
        when(inventoryItemRepository.findAll()).thenReturn(List.of(
            InventoryItem.builder().id(1L).name("Pens").quantity(100).build(),
            InventoryItem.builder().id(2L).name("Notebooks").quantity(50).build()
        ));
        List<InventoryItemResponse> result = inventoryService.getAllItems();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Pens");
    }

    @Test
    void getAllItems_EmptyRepository_ReturnsEmptyList() {
        when(inventoryItemRepository.findAll()).thenReturn(List.of());
        assertThat(inventoryService.getAllItems()).isEmpty();
    }

    @Test
    void addItem_ValidRequest_SavesAndReturnsItem() {
        InventoryItemRequest req = new InventoryItemRequest();
        req.setName("Staplers"); req.setQuantity(20); req.setDescription("Desktop staplers");
        InventoryItem saved = InventoryItem.builder().id(1L).name("Staplers").quantity(20).description("Desktop staplers").build();
        when(inventoryItemRepository.save(any())).thenReturn(saved);
        InventoryItemResponse result = inventoryService.addItem(req);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Staplers");
    }

    @Test
    void updateItem_WhenItemExists_UpdatesAndReturns() {
        InventoryItem existing = InventoryItem.builder().id(1L).name("Pens").quantity(50).build();
        InventoryItemRequest req = new InventoryItemRequest();
        req.setName("Blue Pens"); req.setQuantity(75);
        InventoryItem updated = InventoryItem.builder().id(1L).name("Blue Pens").quantity(75).build();
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(inventoryItemRepository.save(any())).thenReturn(updated);
        InventoryItemResponse result = inventoryService.updateItem(1L, req);
        assertThat(result.getName()).isEqualTo("Blue Pens");
        assertThat(result.getQuantity()).isEqualTo(75);
    }

    @Test
    void updateItem_WhenItemNotFound_ThrowsException() {
        when(inventoryItemRepository.findById(99L)).thenReturn(Optional.empty());
        InventoryItemRequest req = new InventoryItemRequest();
        req.setName("x"); req.setQuantity(1);
        assertThatThrownBy(() -> inventoryService.updateItem(99L, req))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
    }
}
