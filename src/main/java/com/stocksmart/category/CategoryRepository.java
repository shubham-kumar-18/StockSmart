package com.stocksmart.category;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrganization_IdOrderByName(Long organizationId);
    Optional<Category> findByIdAndOrganization_Id(Long id, Long organizationId);
    boolean existsByNameIgnoreCaseAndOrganization_Id(String name, Long organizationId);
}
