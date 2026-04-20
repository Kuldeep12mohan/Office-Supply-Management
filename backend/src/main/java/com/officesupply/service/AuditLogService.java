package com.officesupply.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.officesupply.model.AuditLog;
import com.officesupply.model.User;
import com.officesupply.enums.AuditAction;
import com.officesupply.repository.AuditLogRepository;
import com.officesupply.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public void logRequestCreated(Long requestId, String itemName, Integer quantity) {
        Map<String, Object> details = new HashMap<>();
        details.put("requestId", requestId);
        details.put("itemName", itemName);
        details.put("quantity", quantity);
        
        logAction(AuditAction.REQUEST_CREATED, null, requestId, details);
    }

    public void logRequestApproved(Long requestId, String itemName, Integer quantity, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("requestId", requestId);
        details.put("itemName", itemName);
        details.put("quantity", quantity);
        details.put("reason", reason);
        
        logAction(AuditAction.REQUEST_APPROVED, null, requestId, details);
    }

    public void logRequestRejected(Long requestId, String itemName, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("requestId", requestId);
        details.put("itemName", itemName);
        details.put("reason", reason);
        
        logAction(AuditAction.REQUEST_REJECTED, null, requestId, details);
    }

    public void logInventoryCreated(Long inventoryId, String itemName, Integer quantity) {
        Map<String, Object> details = new HashMap<>();
        details.put("inventoryId", inventoryId);
        details.put("itemName", itemName);
        details.put("quantity", quantity);
        
        logAction(AuditAction.INVENTORY_UPDATED, null, inventoryId, details);
    }

    public void logInventoryUpdated(Long inventoryId, String itemName, Integer quantity) {
        Map<String, Object> details = new HashMap<>();
        details.put("inventoryId", inventoryId);
        details.put("itemName", itemName);
        details.put("quantity", quantity);
        
        logAction(AuditAction.INVENTORY_UPDATED, null, inventoryId, details);
    }

    private void logAction(AuditAction action, User user, Long itemId, Map<String, Object> details) {
        try {
            AuditLog log = AuditLog.builder()
                    .action(action)
                    .actionBy(user)
                    .itemId(itemId)
                    .details(objectMapper.writeValueAsString(details))
                    .createdAt(LocalDateTime.now())
                    .build();
            
            auditLogRepository.save(log);
            log.info("Audit logged: {} for item: {}", action, itemId);
        } catch (Exception e) {
            log.error("Error logging audit action: {}", e.getMessage(), e);
        }
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getAuditLogsByAction(AuditAction action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
    }
}
