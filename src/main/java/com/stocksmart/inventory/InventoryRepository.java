package com.stocksmart.inventory;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    @EntityGraph(attributePaths={"product","location"})
    @Query("select i from Inventory i where i.organization.id=:org order by i.product.name,i.location.name")
    List<Inventory> findWorkspaceInventory(@Param("org") Long organizationId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.organization.id=:org and i.product.id=:product and i.location.id=:location")
    Optional<Inventory> lockRow(@Param("org") Long organizationId,@Param("product") Long productId,@Param("location") Long locationId);
    Optional<Inventory> findByOrganization_IdAndProduct_IdAndLocation_Id(Long organizationId,Long productId,Long locationId);
    @EntityGraph(attributePaths={"product","location"})
    List<Inventory> findAllByOrganization_IdAndProduct_IdOrderByLocation_Name(Long organizationId,Long productId);
    @Query("select coalesce(sum(i.quantity),0) from Inventory i where i.organization.id=:org")
    java.math.BigDecimal totalUnits(@Param("org") Long organizationId);
    boolean existsByOrganization_IdAndLocation_IdAndQuantityGreaterThan(Long organizationId,Long locationId,java.math.BigDecimal quantity);
}
