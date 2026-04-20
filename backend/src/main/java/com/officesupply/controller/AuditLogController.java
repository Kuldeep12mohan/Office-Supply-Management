package com.officesupply.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.officesupply.dto.ApiResponse;
import com.officesupply.service.AuditLogService;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Slf4j
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAuditLogs() {
        var logs = auditLogService.getAuditLogs();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Audit logs retrieved")
                .data(logs)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
