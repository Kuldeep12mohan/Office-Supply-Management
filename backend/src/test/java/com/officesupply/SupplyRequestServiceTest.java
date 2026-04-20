package com.officesupply;

import com.officesupply.dto.RejectRequest;
import com.officesupply.dto.SupplyRequestDto;
import com.officesupply.dto.SupplyRequestResponse;
import com.officesupply.entity.InventoryItem;
import com.officesupply.entity.SupplyRequest;
import com.officesupply.entity.User;
import com.officesupply.repository.InventoryItemRepository;
import com.officesupply.repository.SupplyRequestRepository;
import com.officesupply.repository.UserRepository;
import com.officesupply.service.SupplyRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyRequestServiceTest {

    @Mock private SupplyRequestRepository supplyRequestRepository;
    @Mock private UserRepository userRepository;
    @Mock private InventoryItemRepository inventoryItemRepository;
    @InjectMocks private SupplyRequestService supplyRequestService;

    private User mockEmployee() {
        return User.builder().id(1L).username("employee1").role("EMPLOYEE").build();
    }

    private SupplyRequest mockPendingRequest(User employee) {
        SupplyRequest r = new SupplyRequest();
        r.setId(1L); r.setEmployee(employee); r.setItemName("Pens");
        r.setQuantity(10); r.setStatus("PENDING");
        r.setCreatedAt(LocalDateTime.now()); r.setUpdatedAt(LocalDateTime.now());
        return r;
    }

    @Test
    void createRequest_ValidInput_CreatesRequest() {
        User emp = mockEmployee();
        SupplyRequestDto dto = new SupplyRequestDto();
        dto.setItemName("Pens"); dto.setQuantity(5); dto.setRemarks("Urgent");
        SupplyRequest saved = mockPendingRequest(emp);
        when(userRepository.findByUsername("employee1")).thenReturn(Optional.of(emp));
        when(supplyRequestRepository.save(any())).thenReturn(saved);
        SupplyRequestResponse result = supplyRequestService.createRequest("employee1", dto);
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getEmployeeUsername()).isEqualTo("employee1");
    }

    @Test
    void createRequest_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        SupplyRequestDto dto = new SupplyRequestDto();
        dto.setItemName("Pens"); dto.setQuantity(1);
        assertThatThrownBy(() -> supplyRequestService.createRequest("ghost", dto))
            .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void approveRequest_SufficientInventory_ApprovesAndDecrements() {
        User emp = mockEmployee();
        SupplyRequest req = mockPendingRequest(emp);
        InventoryItem item = InventoryItem.builder().id(1L).name("Pens").quantity(50).build();
        when(supplyRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(inventoryItemRepository.findAll()).thenReturn(List.of(item));
        when(inventoryItemRepository.save(any())).thenReturn(item);
        when(supplyRequestRepository.save(any())).thenReturn(req);
        SupplyRequestResponse result = supplyRequestService.approveRequest(1L);
        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(item.getQuantity()).isEqualTo(40);
    }

    @Test
    void approveRequest_InsufficientInventory_ThrowsException() {
        User emp = mockEmployee();
        SupplyRequest req = mockPendingRequest(emp);
        InventoryItem item = InventoryItem.builder().id(1L).name("Pens").quantity(5).build();
        when(supplyRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(inventoryItemRepository.findAll()).thenReturn(List.of(item));
        assertThatThrownBy(() -> supplyRequestService.approveRequest(1L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Insufficient");
    }

    @Test
    void approveRequest_InventoryItemNotFound_ThrowsException() {
        User emp = mockEmployee();
        SupplyRequest req = mockPendingRequest(emp);
        when(supplyRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(inventoryItemRepository.findAll()).thenReturn(List.of());
        assertThatThrownBy(() -> supplyRequestService.approveRequest(1L))
            .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void approveRequest_AlreadyApproved_ThrowsBadRequest() {
        User emp = mockEmployee();
        SupplyRequest req = mockPendingRequest(emp);
        req.setStatus("APPROVED");
        when(supplyRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        assertThatThrownBy(() -> supplyRequestService.approveRequest(1L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("already");
    }

    @Test
    void rejectRequest_ValidRequest_RejectsWithReason() {
        User emp = mockEmployee();
        SupplyRequest req = mockPendingRequest(emp);
        RejectRequest rr = new RejectRequest(); rr.setReason("Budget constraints");
        when(supplyRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(supplyRequestRepository.save(any())).thenReturn(req);
        SupplyRequestResponse result = supplyRequestService.rejectRequest(1L, rr);
        assertThat(result.getStatus()).isEqualTo("REJECTED");
    }

    @Test
    void rejectRequest_AlreadyRejected_ThrowsBadRequest() {
        User emp = mockEmployee();
        SupplyRequest req = mockPendingRequest(emp);
        req.setStatus("REJECTED");
        when(supplyRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        assertThatThrownBy(() -> supplyRequestService.rejectRequest(1L, null))
            .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getRequests_AdminRole_ReturnsAllRequests() {
        User emp = mockEmployee();
        when(supplyRequestRepository.findAllByOrderByCreatedAtDesc())
            .thenReturn(List.of(mockPendingRequest(emp)));
        List<SupplyRequestResponse> results = supplyRequestService.getRequests("admin", "ADMIN");
        assertThat(results).hasSize(1);
    }

    @Test
    void getRequests_EmployeeRole_ReturnsOwnRequests() {
        User emp = mockEmployee();
        when(userRepository.findByUsername("employee1")).thenReturn(Optional.of(emp));
        when(supplyRequestRepository.findByEmployeeOrderByCreatedAtDesc(emp))
            .thenReturn(List.of(mockPendingRequest(emp)));
        List<SupplyRequestResponse> results = supplyRequestService.getRequests("employee1", "EMPLOYEE");
        assertThat(results).hasSize(1);
    }
}
