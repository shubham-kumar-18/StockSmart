package com.stocksmart.supplier;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    @Query("select s from Supplier s where s.organization.id=:org and (:q is null or lower(s.name) like lower(concat('%',:q,'%')) or lower(coalesce(s.email,'')) like lower(concat('%',:q,'%')) or lower(coalesce(s.phone,'')) like lower(concat('%',:q,'%'))) order by s.name")
    List<Supplier> search(@Param("org") Long organizationId,@Param("q") String query);
    Optional<Supplier> findByIdAndOrganization_Id(Long id,Long organizationId);
    List<Supplier> findAllByOrganization_IdAndActiveTrueOrderByName(Long organizationId);
}
