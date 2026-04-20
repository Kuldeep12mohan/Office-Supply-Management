package com.officesupply.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.officesupply.model.Inventory;
import com.officesupply.model.User;
import com.officesupply.enums.UserRole;
import com.officesupply.exception.ResourceNotFoundException;
import com.officesupply.repository.InventoryRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory testInventory;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        testInventory = Inventory.builder()
                .id(1L)
                .itemName("Printer Paper")
                .quantity(100)
                .reorderLevel(20)
                .lastUpdatedBy(testUser)
                .build();
    }

    @Test
    void testCreateInventory() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        Inventory result = inventoryService.createInventory("Printer Paper", 100, 20, 1L);

        assertNotNull(result);
        assertEquals("Printer Paper", result.getItemName());
        assertEquals(100, result.getQuantity());
        assertEquals(20, result.getReorderLevel());
    }

    @Test
    void testGetInventoryById() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));

        Inventory result = inventoryService.getInventoryById(1L);

        assertNotNull(result);
        assertEquals("Printer Paper", result.getItemName());
    }

    @Test
    void testGetInventoryByIdNotFound() {
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryById(999L));
    }

    @Test
    void testUpdateInventory() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        Inventory result = inventoryService.updateInventory(1L, 150, 30, 1L);

        assertNotNull(result);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testGetInventoryByItemName() {
        when(inventoryRepository.findByItemName("Printer Paper")).thenReturn(Optional.of(testInventory));

        Inventory result = inventoryService.getInventoryByItemName("Printer Paper");

        assertNotNull(result);
        assertEquals("Printer Paper", result.getItemName());
    }
}
