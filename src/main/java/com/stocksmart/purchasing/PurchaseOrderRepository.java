package com.stocksmart.purchasing;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long> {
    @EntityGraph(attributePaths={"supplier","location","items","items.product"})
    @Query("select distinct p from PurchaseOrder p where p.organization.id=:org order by p.createdAt desc,p.id desc")
    List<PurchaseOrder> allForOrganization(@Param("org") Long organizationId);
    @EntityGraph(attributePaths={"supplier","location","items"})
    List<PurchaseOrder> findAllByOrganization_IdAndSupplier_IdOrderByCreatedAtDesc(Long organizationId,Long supplierId);
    @EntityGraph(attributePaths={"supplier","location","items","items.product"})
    Optional<PurchaseOrder> findByIdAndOrganization_Id(Long id,Long organizationId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PurchaseOrder p where p.id=:id and p.organization.id=:org")
    Optional<PurchaseOrder> lockOrder(@Param("id") Long id,@Param("org") Long organizationId);
    boolean existsByOrganization_IdAndLocation_IdAndStatusIn(Long organizationId,Long locationId,Collection<PurchaseOrderStatus> statuses);
}
