package com.stocksmart.category;

import com.stocksmart.organization.Organization;
import jakarta.persistence.*;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(name = "uq_categories_org_name", columnNames = {"organization_id", "name"}))
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false) private Organization organization;
    @Column(nullable = false, length = 80) private String name;
    protected Category() { }
    public Category(Organization organization, String name) { this.organization = organization; this.name = name; }
    public Long getId() { return id; }
    public Long getOrganizationId() { return organization.getId(); }
    public String getName() { return name; }
}
