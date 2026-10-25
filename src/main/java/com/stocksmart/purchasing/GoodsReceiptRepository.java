package com.stocksmart.purchasing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt,Long> {
    boolean existsByOrganization_IdAndReferenceIgnoreCase(Long organizationId,String reference);
}
