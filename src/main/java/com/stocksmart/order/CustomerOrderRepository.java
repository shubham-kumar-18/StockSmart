package com.stocksmart.order;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder,Long> {
    @EntityGraph(attributePaths={"location","items","items.product"})
    @Query("select distinct o from CustomerOrder o where o.organization.id=:org order by o.createdAt desc,o.id desc")
    List<CustomerOrder> findAllWorkspaceOrders(@Param("org") Long organizationId);
    @EntityGraph(attributePaths={"location","items","items.product"})
    Optional<CustomerOrder> findByIdAndOrganization_Id(Long id,Long organizationId);
    long countByOrganization_IdAndStatusIn(Long organizationId,Collection<OrderStatus> statuses);
    boolean existsByOrganization_IdAndLocation_IdAndStatusIn(Long organizationId,Long locationId,Collection<OrderStatus> statuses);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from CustomerOrder o where o.id=:id and o.organization.id=:org")
    Optional<CustomerOrder> lockOrder(@Param("id") Long id,@Param("org") Long organizationId);
}
