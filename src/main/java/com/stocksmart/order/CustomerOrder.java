package com.stocksmart.order;

import com.stocksmart.location.Location;
import com.stocksmart.organization.Organization;
import com.stocksmart.user.AppUser;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name="customer_orders",uniqueConstraints=@UniqueConstraint(name="uq_orders_org_number",columnNames={"organization_id","order_number"}))
public class CustomerOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id",nullable=false) private Organization organization;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="location_id",nullable=false) private Location location;
    @Column(name="order_number",nullable=false,length=40) private String orderNumber;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private OrderStatus status;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal subtotal;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="created_by",nullable=false) private AppUser createdBy;
    @Column(name="created_at",insertable=false,updatable=false) private LocalDateTime createdAt;
    @Column(name="updated_at",insertable=false,updatable=false) private LocalDateTime updatedAt;
    @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) private List<OrderItem> items=new ArrayList<>();
    protected CustomerOrder() { }
    public CustomerOrder(Organization org,Location location,String orderNumber,AppUser creator){this.organization=org;this.location=location;this.orderNumber=orderNumber;this.createdBy=creator;this.status=OrderStatus.DRAFT;this.subtotal=BigDecimal.ZERO;}
    public void addItem(OrderItem item){items.add(item);subtotal=subtotal.add(item.getLineTotal());}
    public void setStatus(OrderStatus status){this.status=status;}
    public Long getId(){return id;} public Long getOrganizationId(){return organization.getId();} public Location getLocation(){return location;} public String getOrderNumber(){return orderNumber;} public OrderStatus getStatus(){return status;} public BigDecimal getSubtotal(){return subtotal;} public LocalDateTime getCreatedAt(){return createdAt;} public List<OrderItem> getItems(){return items;}
}
