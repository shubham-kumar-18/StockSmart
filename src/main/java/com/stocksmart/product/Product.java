package com.stocksmart.product;

import com.stocksmart.category.Category;
import com.stocksmart.organization.Organization;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", uniqueConstraints = {
    @UniqueConstraint(name="uq_products_org_sku", columnNames={"organization_id", "sku"}),
    @UniqueConstraint(name="uq_products_org_barcode", columnNames={"organization_id", "barcode"})
})
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="organization_id", nullable=false) private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="category_id") private Category category;
    @Column(nullable=false, length=160) private String name;
    @Column(nullable=false, length=80) private String sku;
    @Column(columnDefinition="TEXT") private String description;
    @Column(name="selling_price", nullable=false, precision=12, scale=2) private BigDecimal sellingPrice;
    @Column(name="cost_price", precision=12, scale=2) private BigDecimal costPrice;
    @Column(length=120) private String barcode;
    @Column(nullable=false, length=30) private String unit;
    @Column(name="reorder_level", nullable=false, precision=12, scale=3) private BigDecimal reorderLevel;
    @Column(nullable=false) private boolean active;
    @Column(name="created_at", insertable=false, updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at", insertable=false, updatable=false) private LocalDateTime updatedAt;
    protected Product() { }
    public Product(Organization org, Category category, ProductForm form) { update(category, form); this.organization=org; }
    public void update(Category category, ProductForm form) {
        this.category=category; this.name=form.getName().trim(); this.sku=form.getSku().trim();
        this.description=blankToNull(form.getDescription()); this.sellingPrice=form.getSellingPrice();
        this.costPrice=form.getCostPrice(); this.barcode=blankToNull(form.getBarcode()); this.unit=form.getUnit().trim();
        this.reorderLevel=form.getReorderLevel(); this.active=form.isActive();
    }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    public Long getId() { return id; }
    public Long getOrganizationId() { return organization.getId(); }
    public Category getCategory() { return category; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public BigDecimal getCostPrice() { return costPrice; }
    public String getBarcode() { return barcode; }
    public String getUnit() { return unit; }
    public BigDecimal getReorderLevel() { return reorderLevel; }
    public boolean isActive() { return active; }
}
