package com.stocksmart.purchasing;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem,Long> {
    Optional<PurchaseOrderItem> findByIdAndPurchaseOrder_Id(Long id,Long purchaseOrderId);
}
