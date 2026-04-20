package com.officesupply.service;

import com.officesupply.dto.RejectRequest;
import com.officesupply.dto.SupplyRequestDto;
import com.officesupply.dto.SupplyRequestResponse;
import com.officesupply.entity.InventoryItem;
import com.officesupply.entity.SupplyRequest;
import com.officesupply.entity.User;
import com.officesupply.repository.InventoryItemRepository;
import com.officesupply.repository.SupplyRequestRepository;
import com.officesupply.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyRequestService {

    private final SupplyRequestRepository supplyRequestRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Transactional
    public SupplyRequestResponse createRequest(String username, SupplyRequestDto dto) {
        User employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SupplyRequest request = SupplyRequest.builder()
                .employee(employee)
                .itemName(dto.getItemName())
                .quantity(dto.getQuantity())
                .remarks(dto.getRemarks())
                .status("PENDING")
                .build();
        return toResponse(supplyRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<SupplyRequestResponse> getRequests(String username, String role) {
        if ("ADMIN".equals(role)) {
            return supplyRequestRepository.findAllByOrderByCreatedAtDesc()
                    .stream().map(this::toResponse).collect(Collectors.toList());
        }
        User employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return supplyRequestRepository.findByEmployeeOrderByCreatedAtDesc(employee)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public SupplyRequestResponse approveRequest(Long id) {
        SupplyRequest request = supplyRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Request is already " + request.getStatus().toLowerCase());
        }

        InventoryItem item = inventoryItemRepository.findAll().stream()
                .filter(i -> i.getName().equalsIgnoreCase(request.getItemName()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Inventory item '" + request.getItemName() + "' not found"));

        if (item.getQuantity() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Insufficient inventory. Available: " + item.getQuantity() +
                    ", Requested: " + request.getQuantity());
        }

        item.setQuantity(item.getQuantity() - request.getQuantity());
        inventoryItemRepository.save(item);

        request.setStatus("APPROVED");
        request.setUpdatedAt(LocalDateTime.now());
        return toResponse(supplyRequestRepository.save(request));
    }

    @Transactional
    public SupplyRequestResponse rejectRequest(Long id, RejectRequest rejectRequest) {
        SupplyRequest request = supplyRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Request is already " + request.getStatus().toLowerCase());
        }

        request.setStatus("REJECTED");
        request.setRejectionReason(rejectRequest != null ? rejectRequest.getReason() : null);
        request.setUpdatedAt(LocalDateTime.now());
        return toResponse(supplyRequestRepository.save(request));
    }

    private SupplyRequestResponse toResponse(SupplyRequest r) {
        return SupplyRequestResponse.builder()
                .id(r.getId())
                .employeeUsername(r.getEmployee().getUsername())
                .itemName(r.getItemName())
                .quantity(r.getQuantity())
                .remarks(r.getRemarks())
                .status(r.getStatus())
                .rejectionReason(r.getRejectionReason())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
