package com.stocksmart.purchasing;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="goods_receipt_items")
public class GoodsReceiptItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="goods_receipt_id",nullable=false) private GoodsReceipt receipt;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="purchase_order_item_id",nullable=false) private PurchaseOrderItem purchaseOrderItem;
    @Column(nullable=false,precision=12,scale=3) private BigDecimal quantity;
    protected GoodsReceiptItem() { }
    public GoodsReceiptItem(GoodsReceipt receipt,PurchaseOrderItem item,BigDecimal quantity){this.receipt=receipt;this.purchaseOrderItem=item;this.quantity=quantity;}
}
