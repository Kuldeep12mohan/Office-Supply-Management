package com.officesupply.repository;

import com.officesupply.model.SupplyRequest;
import com.officesupply.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long> {
    List<SupplyRequest> findByRequestedByIdOrderByCreatedAtDesc(Long userId);
    List<SupplyRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);
    List<SupplyRequest> findAllByOrderByCreatedAtDesc();
    long countByStatus(RequestStatus status);
}
