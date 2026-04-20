package com.officesupply.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.officesupply.dto.SupplyRequestDto;
import com.officesupply.dto.ApiResponse;
import com.officesupply.service.SupplyRequestService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class SupplyRequestController {

    private final SupplyRequestService requestService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createRequest(
            @Valid @RequestBody SupplyRequestDto requestDto,
            HttpSession session) {
        log.info("Creating supply request for item: {}", requestDto.getItemName());
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Unauthorized")
                            .timestamp(LocalDateTime.now().toString())
                            .build());
        }

        var request = requestService.createRequest(
                requestDto.getItemName(),
                requestDto.getQuantity(),
                requestDto.getRemarks(),
                userId
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .success(true)
                        .message("Request created successfully")
                        .data(request)
                        .timestamp(LocalDateTime.now().toString())
                        .build());
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<?>> getMyRequests(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<SupplyRequestDto> requests = requestService.getMyRequests(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Requests retrieved")
                .data(requests)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllRequests() {
        List<SupplyRequestDto> requests = requestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("All requests retrieved")
                .data(requests)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<?>> getPendingRequests() {
        List<SupplyRequestDto> requests = requestService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Pending requests retrieved")
                .data(requests)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getRequestById(@PathVariable Long id) {
        var request = requestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Request retrieved")
                .data(request)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approveRequest(
            @PathVariable Long id,
            @RequestParam String reason,
            HttpSession session) {
        log.info("Approving request: {}", id);
        
        Long adminId = (Long) session.getAttribute("userId");
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var approved = requestService.approveRequest(id, adminId, reason);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Request approved successfully")
                .data(approved)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<?>> rejectRequest(
            @PathVariable Long id,
            @RequestParam String reason,
            HttpSession session) {
        log.info("Rejecting request: {}", id);
        
        Long adminId = (Long) session.getAttribute("userId");
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var rejected = requestService.rejectRequest(id, adminId, reason);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Request rejected successfully")
                .data(rejected)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
