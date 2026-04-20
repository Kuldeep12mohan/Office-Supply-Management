package com.officesupply.controller;

import com.officesupply.dto.RejectRequest;
import com.officesupply.dto.SupplyRequestDto;
import com.officesupply.dto.SupplyRequestResponse;
import com.officesupply.service.SupplyRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class SupplyRequestController {

    private final SupplyRequestService supplyRequestService;

    @GetMapping
    public ResponseEntity<List<SupplyRequestResponse>> getRequests(Authentication auth) {
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst().orElse("EMPLOYEE");
        return ResponseEntity.ok(supplyRequestService.getRequests(auth.getName(), role));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<SupplyRequestResponse> createRequest(
            @Valid @RequestBody SupplyRequestDto dto, Authentication auth) {
        return ResponseEntity.ok(supplyRequestService.createRequest(auth.getName(), dto));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplyRequestResponse> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(supplyRequestService.approveRequest(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplyRequestResponse> rejectRequest(
            @PathVariable Long id,
            @RequestBody(required = false) RejectRequest rejectRequest) {
        return ResponseEntity.ok(supplyRequestService.rejectRequest(id, rejectRequest));
    }
}
