package com.stocksmart.order;

import com.stocksmart.product.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id",nullable=false) private CustomerOrder order;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="product_id",nullable=false) private Product product;
    @Column(name="product_name",nullable=false,length=160) private String productName;
    @Column(nullable=false,length=80) private String sku;
    @Column(nullable=false,precision=12,scale=3) private BigDecimal quantity;
    @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
    @Column(name="line_total",nullable=false,precision=14,scale=2) private BigDecimal lineTotal;
    protected OrderItem() { }
    public OrderItem(CustomerOrder order,Product product,BigDecimal quantity,BigDecimal price){this.order=order;this.product=product;this.productName=product.getName();this.sku=product.getSku();this.quantity=quantity;this.unitPrice=price;this.lineTotal=quantity.multiply(price).setScale(2,java.math.RoundingMode.HALF_UP);}
    public Product getProduct(){return product;} public BigDecimal getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getLineTotal(){return lineTotal;} public String getProductName(){return productName;} public String getSku(){return sku;}
}
