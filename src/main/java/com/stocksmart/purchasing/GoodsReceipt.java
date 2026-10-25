package com.stocksmart.purchasing;

import com.stocksmart.organization.Organization;
import com.stocksmart.user.AppUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name="goods_receipts",uniqueConstraints=@UniqueConstraint(name="uq_goods_receipts_reference",columnNames={"organization_id","receipt_reference"}))
public class GoodsReceipt {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id",nullable=false) private Organization organization;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="purchase_order_id",nullable=false) private PurchaseOrder purchaseOrder;
    @Column(name="receipt_reference",nullable=false,length=120) private String reference;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="received_by",nullable=false) private AppUser receivedBy;
    @Column(name="created_at",insertable=false,updatable=false) private LocalDateTime createdAt;
    @OneToMany(mappedBy="receipt",cascade=CascadeType.ALL,orphanRemoval=true) private List<GoodsReceiptItem> items=new ArrayList<>();
    protected GoodsReceipt() { }
    public GoodsReceipt(Organization org,PurchaseOrder po,String reference,AppUser user){this.organization=org;this.purchaseOrder=po;this.reference=reference;this.receivedBy=user;}
    public void addItem(GoodsReceiptItem item){items.add(item);}
}
