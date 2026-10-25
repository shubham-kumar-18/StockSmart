package com.stocksmart.inventory;

import com.stocksmart.location.Location;
import com.stocksmart.organization.Organization;
import com.stocksmart.product.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="inventory", uniqueConstraints=@UniqueConstraint(name="uq_inventory_org_product_location",columnNames={"organization_id","product_id","location_id"}))
public class Inventory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id",nullable=false) private Organization organization;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="location_id",nullable=false) private Location location;
    @Column(nullable=false,precision=12,scale=3) private BigDecimal quantity;
    @Column(nullable=false,precision=12,scale=3) private BigDecimal reservedQuantity=BigDecimal.ZERO;
    @Version @Column(nullable=false) private Long version;
    @Column(name="updated_at",insertable=false,updatable=false) private LocalDateTime updatedAt;
    protected Inventory() { }
    public Inventory(Organization org,Product product,Location location){this.organization=org;this.product=product;this.location=location;this.quantity=BigDecimal.ZERO;}
    public void add(BigDecimal amount){quantity=quantity.add(amount);}
    public void subtract(BigDecimal amount){quantity=quantity.subtract(amount);}
    public void reserve(BigDecimal amount){reservedQuantity=reservedQuantity.add(amount);}
    public void release(BigDecimal amount){reservedQuantity=reservedQuantity.subtract(amount);}
    public Long getId(){return id;} public Long getOrganizationId(){return organization.getId();}
    public Product getProduct(){return product;} public Location getLocation(){return location;}
    public BigDecimal getQuantity(){return quantity;} public LocalDateTime getUpdatedAt(){return updatedAt;}
    public BigDecimal getReservedQuantity(){return reservedQuantity;}
    public BigDecimal getAvailableQuantity(){return quantity.subtract(reservedQuantity);}
}
