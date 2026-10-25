package com.stocksmart.location;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location,Long> {
    List<Location> findAllByOrganization_IdOrderByName(Long organizationId);
    Optional<Location> findByIdAndOrganization_Id(Long id,Long organizationId);
    boolean existsByCodeIgnoreCaseAndOrganization_Id(String code,Long organizationId);
    boolean existsByCodeIgnoreCaseAndOrganization_IdAndIdNot(String code,Long organizationId,Long id);
    long countByOrganization_Id(Long organizationId);
}
