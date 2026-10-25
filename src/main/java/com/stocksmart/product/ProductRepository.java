package com.stocksmart.product;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p left join fetch p.category where p.organization.id=:org order by p.name")
    List<Product> findWorkspaceProducts(@Param("org") Long org);
    @Query("select p from Product p left join fetch p.category where p.organization.id=:org and (lower(p.name) like lower(concat('%',:q,'%')) or lower(p.sku) like lower(concat('%',:q,'%')) or lower(coalesce(p.barcode,'')) like lower(concat('%',:q,'%'))) order by p.name")
    List<Product> searchWorkspaceProducts(@Param("org") Long org, @Param("q") String q);
    @Query("select p from Product p left join fetch p.category where p.organization.id=:org and p.active=true order by p.name")
    List<Product> findActiveWorkspaceProducts(@Param("org") Long org);
    @Query("select p from Product p left join fetch p.category where p.organization.id=:org and p.active=false order by p.name")
    List<Product> findInactiveWorkspaceProducts(@Param("org") Long org);
    @Query("select p from Product p left join fetch p.category where p.organization.id=:org and p.active=true and (lower(p.name) like lower(concat('%',:q,'%')) or lower(p.sku) like lower(concat('%',:q,'%')) or lower(coalesce(p.barcode,'')) like lower(concat('%',:q,'%'))) order by p.name")
    List<Product> searchActiveWorkspaceProducts(@Param("org") Long org, @Param("q") String q);
    @Query("select p from Product p left join fetch p.category where p.organization.id=:org and p.active=false and (lower(p.name) like lower(concat('%',:q,'%')) or lower(p.sku) like lower(concat('%',:q,'%')) or lower(coalesce(p.barcode,'')) like lower(concat('%',:q,'%'))) order by p.name")
    List<Product> searchInactiveWorkspaceProducts(@Param("org") Long org, @Param("q") String q);
    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndOrganization_Id(Long id, Long organizationId);
    boolean existsBySkuIgnoreCaseAndOrganization_Id(String sku, Long organizationId);
    boolean existsByBarcodeIgnoreCaseAndOrganization_Id(String barcode, Long organizationId);
    boolean existsBySkuIgnoreCaseAndOrganization_IdAndIdNot(String sku, Long organizationId, Long id);
    boolean existsByBarcodeIgnoreCaseAndOrganization_IdAndIdNot(String barcode, Long organizationId, Long id);
    long countByOrganization_Id(Long organizationId);
    Optional<Product> findByBarcodeIgnoreCaseAndOrganization_Id(String barcode,Long organizationId);
    long countByOrganization_IdAndActiveTrue(Long organizationId);
}
