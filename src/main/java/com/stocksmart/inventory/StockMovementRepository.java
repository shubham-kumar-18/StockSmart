package com.stocksmart.inventory;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface StockMovementRepository extends JpaRepository<StockMovement,Long> {
    @EntityGraph(attributePaths={"product","sourceLocation","destinationLocation"})
    @Query("select m from StockMovement m where m.organization.id=:org order by m.createdAt desc,m.id desc")
    List<StockMovement> recent(@Param("org") Long organizationId,org.springframework.data.domain.Pageable pageable);
    @EntityGraph(attributePaths={"product","sourceLocation","destinationLocation"})
    List<StockMovement> findAllByOrganization_IdAndCreatedAtBetweenOrderByCreatedAtDesc(Long organizationId,java.time.LocalDateTime from,java.time.LocalDateTime to);
    boolean existsByOrganization_IdAndProduct_IdAndMovementType(Long organizationId,Long productId,MovementType type);
    boolean existsByOrganization_IdAndProduct_IdAndMovementTypeAndDestinationLocation_Id(Long organizationId,Long productId,MovementType type,Long locationId);
    boolean existsByOrganization_IdAndMovementTypeAndProduct_IdAndReferenceIgnoreCase(Long organizationId,MovementType type,Long productId,String reference);
}
