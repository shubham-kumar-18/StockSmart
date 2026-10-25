package com.stocksmart.purchasing;

import com.stocksmart.location.Location;
import com.stocksmart.organization.Organization;
import com.stocksmart.supplier.Supplier;
import com.stocksmart.user.AppUser;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name="purchase_orders",uniqueConstraints=@UniqueConstraint(name="uq_purchase_orders_org_number",columnNames={"organization_id","order_number"}))
public class PurchaseOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id",nullable=false) private Organization organization;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="supplier_id",nullable=false) private Supplier supplier;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="location_id",nullable=false) private Location location;
    @Column(name="order_number",nullable=false,length=40) private String orderNumber;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=24) private PurchaseOrderStatus status;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal total=BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="created_by",nullable=false) private AppUser createdBy;
    @Column(name="created_at",insertable=false,updatable=false) private LocalDateTime createdAt;
    @OneToMany(mappedBy="purchaseOrder",cascade=CascadeType.ALL,orphanRemoval=true) private List<PurchaseOrderItem> items=new ArrayList<>();
    protected PurchaseOrder() { }
    public PurchaseOrder(Organization org,Supplier supplier,Location location,String number,AppUser user){this.organization=org;this.supplier=supplier;this.location=location;this.orderNumber=number;this.createdBy=user;this.status=PurchaseOrderStatus.DRAFT;}
    public void addItem(PurchaseOrderItem item){items.add(item);total=total.add(item.getLineTotal());}
    public void setStatus(PurchaseOrderStatus status){this.status=status;}
    public Long getId(){return id;} public Long getOrganizationId(){return organization.getId();} public Supplier getSupplier(){return supplier;} public Location getLocation(){return location;} public String getOrderNumber(){return orderNumber;} public PurchaseOrderStatus getStatus(){return status;} public BigDecimal getTotal(){return total;} public LocalDateTime getCreatedAt(){return createdAt;} public List<PurchaseOrderItem> getItems(){return items;}
}
