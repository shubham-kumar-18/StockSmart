package com.stocksmart.purchasing;

import com.stocksmart.product.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="purchase_order_items")
public class PurchaseOrderItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="purchase_order_id",nullable=false) private PurchaseOrder purchaseOrder;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
    @Column(name="product_name",nullable=false,length=160) private String productName;
    @Column(nullable=false,length=80) private String sku;
    @Column(name="ordered_quantity",nullable=false,precision=12,scale=3) private BigDecimal orderedQuantity;
    @Column(name="received_quantity",nullable=false,precision=12,scale=3) private BigDecimal receivedQuantity=BigDecimal.ZERO;
    @Column(name="unit_cost",nullable=false,precision=12,scale=2) private BigDecimal unitCost;
    @Column(name="line_total",nullable=false,precision=14,scale=2) private BigDecimal lineTotal;
    protected PurchaseOrderItem() { }
    public PurchaseOrderItem(PurchaseOrder order,Product product,BigDecimal quantity,BigDecimal cost){this.purchaseOrder=order;this.product=product;this.productName=product.getName();this.sku=product.getSku();this.orderedQuantity=quantity;this.unitCost=cost;this.lineTotal=quantity.multiply(cost).setScale(2,java.math.RoundingMode.HALF_UP);}
    public void receive(BigDecimal q){receivedQuantity=receivedQuantity.add(q);}
    public Long getId(){return id;} public Long getProductId(){return product.getId();} public Product getProduct(){return product;} public String getProductName(){return productName;} public String getSku(){return sku;} public BigDecimal getOrderedQuantity(){return orderedQuantity;} public BigDecimal getReceivedQuantity(){return receivedQuantity;} public BigDecimal getOutstanding(){return orderedQuantity.subtract(receivedQuantity);} public BigDecimal getUnitCost(){return unitCost;} public BigDecimal getLineTotal(){return lineTotal;}
}
