package com.officesupply.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.officesupply.model.SupplyRequest;
import com.officesupply.model.Inventory;
import com.officesupply.model.User;
import com.officesupply.enums.RequestStatus;
import com.officesupply.enums.UserRole;
import com.officesupply.exception.InsufficientInventoryException;
import com.officesupply.repository.SupplyRequestRepository;
import com.officesupply.repository.InventoryRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyRequestServiceTest {

    @Mock
    private SupplyRequestRepository requestRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private SupplyRequestService requestService;

    private User testUser;
    private SupplyRequest testRequest;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("employee1")
                .role(UserRole.EMPLOYEE)
                .build();

        testRequest = SupplyRequest.builder()
                .id(1L)
                .itemName("Printer Paper")
                .quantity(10)
                .status(RequestStatus.PENDING)
                .requestedBy(testUser)
                .build();

        testInventory = Inventory.builder()
                .id(1L)
                .itemName("Printer Paper")
                .quantity(100)
                .reorderLevel(20)
                .build();
    }

    @Test
    void testCreateRequest() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(requestRepository.save(any(SupplyRequest.class))).thenReturn(testRequest);

        SupplyRequest result = requestService.createRequest("Printer Paper", 10, "Office use", 1L);

        assertNotNull(result);
        assertEquals("Printer Paper", result.getItemName());
        assertEquals(10, result.getQuantity());
        assertEquals(RequestStatus.PENDING, result.getStatus());
    }

    @Test
    void testApproveRequestSuccess() {
        User admin = User.builder().id(2L).username("admin").role(UserRole.ADMIN).build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userService.getUserById(2L)).thenReturn(admin);
        when(inventoryRepository.findByItemName("Printer Paper")).thenReturn(Optional.of(testInventory));
        when(requestRepository.save(any(SupplyRequest.class))).thenReturn(testRequest);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        SupplyRequest result = requestService.approveRequest(1L, 2L, "Approved");

        assertNotNull(result);
        assertEquals(RequestStatus.APPROVED, result.getStatus());
        assertEquals(admin, result.getApprovedBy());
    }

    @Test
    void testApproveRequestInsufficientInventory() {
        User admin = User.builder().id(2L).username("admin").build();
        testInventory.setQuantity(5); // Less than requested

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userService.getUserById(2L)).thenReturn(admin);
        when(inventoryRepository.findByItemName("Printer Paper")).thenReturn(Optional.of(testInventory));

        assertThrows(InsufficientInventoryException.class, () -> 
            requestService.approveRequest(1L, 2L, "Approved")
        );
    }

    @Test
    void testRejectRequest() {
        User admin = User.builder().id(2L).username("admin").build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(userService.getUserById(2L)).thenReturn(admin);
        when(requestRepository.save(any(SupplyRequest.class))).thenReturn(testRequest);

        SupplyRequest result = requestService.rejectRequest(1L, 2L, "Out of stock");

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
    }

    @Test
    void testGetPendingCount() {
        when(requestRepository.countByStatus(RequestStatus.PENDING)).thenReturn(5L);

        long count = requestService.getPendingCount();

        assertEquals(5L, count);
    }
}
