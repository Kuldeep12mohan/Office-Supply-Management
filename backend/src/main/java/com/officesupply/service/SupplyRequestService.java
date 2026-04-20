package com.officesupply.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.officesupply.model.SupplyRequest;
import com.officesupply.model.User;
import com.officesupply.model.Inventory;
import com.officesupply.enums.RequestStatus;
import com.officesupply.exception.ResourceNotFoundException;
import com.officesupply.exception.InsufficientInventoryException;
import com.officesupply.repository.SupplyRequestRepository;
import com.officesupply.repository.InventoryRepository;
import com.officesupply.dto.SupplyRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplyRequestService {
    
    private final SupplyRequestRepository requestRepository;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public SupplyRequest createRequest(String itemName, Integer quantity, String remarks, Long userId) {
        log.info("Creating supply request for item: {} by user: {}", itemName, userId);
        
        User user = userService.getUserById(userId);
        
        SupplyRequest request = SupplyRequest.builder()
                .itemName(itemName)
                .quantity(quantity)
                .remarks(remarks)
                .status(RequestStatus.PENDING)
                .requestedBy(user)
                .createdAt(LocalDateTime.now())
                .build();
        
        SupplyRequest saved = requestRepository.save(request);
        auditLogService.logRequestCreated(saved.getId(), saved.getItemName(), saved.getQuantity());
        
        return saved;
    }

    public SupplyRequest getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));
    }

    public List<SupplyRequestDto> getMyRequests(Long userId) {
        return requestRepository.findByRequestedByIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SupplyRequestDto> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SupplyRequestDto> getPendingRequests() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(RequestStatus.PENDING)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SupplyRequest approveRequest(Long requestId, Long adminId, String reason) {
        log.info("Approving request: {} by admin: {}", requestId, adminId);
        
        SupplyRequest request = getRequestById(requestId);
        User admin = userService.getUserById(adminId);
        
        // Check inventory availability
        Inventory inventory = inventoryRepository.findByItemName(request.getItemName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found in inventory: " + request.getItemName()));
        
        if (inventory.getQuantity() < request.getQuantity()) {
            throw new InsufficientInventoryException(
                    "Insufficient inventory for " + request.getItemName() +
                    ". Available: " + inventory.getQuantity() +
                    ", Requested: " + request.getQuantity());
        }
        
        // Update request status
        request.setStatus(RequestStatus.APPROVED);
        request.setApprovedBy(admin);
        request.setApprovalReason(reason);
        request.setApprovedAt(LocalDateTime.now());
        
        // Deduct from inventory
        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setLastUpdatedBy(admin);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
        
        SupplyRequest approved = requestRepository.save(request);
        auditLogService.logRequestApproved(approved.getId(), approved.getItemName(), 
                                           approved.getQuantity(), reason);
        
        return approved;
    }

    public SupplyRequest rejectRequest(Long requestId, Long adminId, String reason) {
        log.info("Rejecting request: {} by admin: {}", requestId, adminId);
        
        SupplyRequest request = getRequestById(requestId);
        User admin = userService.getUserById(adminId);
        
        request.setStatus(RequestStatus.REJECTED);
        request.setApprovedBy(admin);
        request.setRejectionReason(reason);
        request.setRejectedAt(LocalDateTime.now());
        
        SupplyRequest rejected = requestRepository.save(request);
        auditLogService.logRequestRejected(rejected.getId(), rejected.getItemName(), reason);
        
        return rejected;
    }

    public long getPendingCount() {
        return requestRepository.countByStatus(RequestStatus.PENDING);
    }

    private SupplyRequestDto convertToDto(SupplyRequest request) {
        return SupplyRequestDto.builder()
                .id(request.getId())
                .itemName(request.getItemName())
                .quantity(request.getQuantity())
                .remarks(request.getRemarks())
                .status(request.getStatus().name())
                .requestedBy(request.getRequestedBy().getFullName())
                .approvedBy(request.getApprovedBy() != null ? request.getApprovedBy().getFullName() : null)
                .approvalReason(request.getApprovalReason())
                .rejectionReason(request.getRejectionReason())
                .createdAt(request.getCreatedAt().toString())
                .approvedAt(request.getApprovedAt() != null ? request.getApprovedAt().toString() : null)
                .rejectedAt(request.getRejectedAt() != null ? request.getRejectedAt().toString() : null)
                .build();
    }
}
