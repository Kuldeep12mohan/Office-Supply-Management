package com.officesupply.repository;

import com.officesupply.entity.SupplyRequest;
import com.officesupply.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long> {
    List<SupplyRequest> findByEmployeeOrderByCreatedAtDesc(User employee);
    List<SupplyRequest> findAllByOrderByCreatedAtDesc();
}
