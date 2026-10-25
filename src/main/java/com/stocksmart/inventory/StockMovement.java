package com.stocksmart.inventory;

import com.stocksmart.location.Location;
import com.stocksmart.organization.Organization;
import com.stocksmart.product.Product;
import com.stocksmart.user.AppUser;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="stock_movements")
public class StockMovement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id",nullable=false) private Organization organization;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
    @Enumerated(EnumType.STRING) @Column(name="movement_type",nullable=false,length=30) private MovementType movementType;
    @Column(nullable=false,precision=12,scale=3) private BigDecimal quantity;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="source_location_id") private Location sourceLocation;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="destination_location_id") private Location destinationLocation;
    @Column(length=120) private String reference;
    @Column(length=500) private String reason;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="performed_by",nullable=false) private AppUser performedBy;
    @Column(name="created_at",insertable=false,updatable=false) private LocalDateTime createdAt;
    protected StockMovement() { }
    public StockMovement(Organization org,Product product,MovementType type,BigDecimal quantity,Location source,Location destination,String reference,String reason,AppUser actor){this.organization=org;this.product=product;this.movementType=type;this.quantity=quantity;this.sourceLocation=source;this.destinationLocation=destination;this.reference=reference;this.reason=reason;this.performedBy=actor;}
    public Long getId(){return id;} public Product getProduct(){return product;} public MovementType getMovementType(){return movementType;} public BigDecimal getQuantity(){return quantity;} public Location getSourceLocation(){return sourceLocation;} public Location getDestinationLocation(){return destinationLocation;} public String getReference(){return reference;} public String getReason(){return reason;} public LocalDateTime getCreatedAt(){return createdAt;}
}
