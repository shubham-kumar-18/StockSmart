package com.stocksmart.purchasing;

import com.stocksmart.inventory.*;
import com.stocksmart.location.*;
import com.stocksmart.organization.OrganizationRepository;
import com.stocksmart.product.*;
import com.stocksmart.supplier.*;
import com.stocksmart.user.AppUserRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchasingService {
    private final PurchaseOrderRepository orders;private final PurchaseOrderItemRepository items;private final GoodsReceiptRepository receipts;
    private final SupplierRepository suppliers;private final LocationRepository locations;private final ProductRepository products;
    private final OrganizationRepository organizations;private final AppUserRepository users;private final InventoryService inventory;
    public PurchasingService(PurchaseOrderRepository orders,PurchaseOrderItemRepository items,GoodsReceiptRepository receipts,SupplierRepository suppliers,LocationRepository locations,ProductRepository products,OrganizationRepository organizations,AppUserRepository users,InventoryService inventory){this.orders=orders;this.items=items;this.receipts=receipts;this.suppliers=suppliers;this.locations=locations;this.products=products;this.organizations=organizations;this.users=users;this.inventory=inventory;}
    @Transactional(readOnly=true) public List<PurchaseOrder> all(Long org){return orders.allForOrganization(org);}
    @Transactional(readOnly=true) public PurchaseOrder get(Long org,Long id){return orders.findByIdAndOrganization_Id(id,org).orElseThrow(()->new NoSuchElementException("Purchase order not found"));}
    @Transactional public void create(Long org,Long actorId,PurchaseOrderForm form){
        var supplier=suppliers.findByIdAndOrganization_Id(form.getSupplierId(),org).orElseThrow(()->new NoSuchElementException("Supplier not found"));
        var location=locations.findByIdAndOrganization_Id(form.getLocationId(),org).orElseThrow(()->new NoSuchElementException("Location not found"));
        if(!supplier.isActive()||!location.isActive())throw new IllegalArgumentException("Select an active supplier and location.");
        var po=new PurchaseOrder(organizations.findById(org).orElseThrow(),supplier,location,newNumber(),users.findById(actorId).orElseThrow());
        if(form.getItems()==null||form.getItems().isEmpty())throw new IllegalArgumentException("Add at least one product.");
        var seen=new HashSet<Long>();
        for(var line:form.getItems()){
            if(line.getProductId()==null||line.getQuantity()==null||line.getUnitCost()==null)throw new IllegalArgumentException("Complete every product line.");
            if(!seen.add(line.getProductId()))throw new IllegalArgumentException("A product can appear only once on a purchase order.");
            var product=products.findByIdAndOrganization_Id(line.getProductId(),org).orElseThrow(()->new NoSuchElementException("Product not found"));
            if(!product.isActive())throw new IllegalArgumentException("Inactive products cannot be ordered.");
            po.addItem(new PurchaseOrderItem(po,product,line.getQuantity(),line.getUnitCost()));
        }
        orders.save(po);
    }
    @Transactional public void place(Long org,Long id){var po=lock(org,id);requireStatus(po,PurchaseOrderStatus.DRAFT,"Only draft purchase orders can be placed.");po.setStatus(PurchaseOrderStatus.ORDERED);}
    @Transactional public void cancel(Long org,Long id){var po=lock(org,id);if(po.getStatus()!=PurchaseOrderStatus.DRAFT&&po.getStatus()!=PurchaseOrderStatus.ORDERED)throw new IllegalArgumentException("Only an unreceived purchase order can be cancelled.");po.setStatus(PurchaseOrderStatus.CANCELLED);}
    @Transactional public void receive(Long org,Long actorId,Long orderId,GoodsReceiptForm form){
        var po=lock(org,orderId);if(po.getStatus()!=PurchaseOrderStatus.ORDERED&&po.getStatus()!=PurchaseOrderStatus.PARTIALLY_RECEIVED)throw new IllegalArgumentException("Only an ordered purchase order can receive stock.");
        var item=items.findByIdAndPurchaseOrder_Id(form.getItemId(),po.getId()).orElseThrow(()->new NoSuchElementException("Purchase order item not found"));
        if(form.getQuantity().compareTo(item.getOutstanding())>0)throw new IllegalArgumentException("Receipt quantity exceeds the outstanding ordered quantity.");
        String reference=form.getReference().trim();if(receipts.existsByOrganization_IdAndReferenceIgnoreCase(org,reference))throw new IllegalArgumentException("This receipt reference has already been used.");
        var receipt=receipts.saveAndFlush(new GoodsReceipt(organizations.findById(org).orElseThrow(),po,reference,users.findById(actorId).orElseThrow()));
        receipt.addItem(new GoodsReceiptItem(receipt,item,form.getQuantity()));item.receive(form.getQuantity());
        var stock=new StockOperationForm();stock.setProductId(item.getProductId());stock.setLocationId(po.getLocation().getId());stock.setQuantity(form.getQuantity());stock.setReference(reference);stock.setReason("Purchase order "+po.getOrderNumber());
        inventory.receiveForPurchaseOrder(org,actorId,stock);
        boolean complete=po.getItems().stream().allMatch(line->line.getOutstanding().signum()==0);
        po.setStatus(complete?PurchaseOrderStatus.RECEIVED:PurchaseOrderStatus.PARTIALLY_RECEIVED);
    }
    private PurchaseOrder lock(Long org,Long id){return orders.lockOrder(id,org).orElseThrow(()->new NoSuchElementException("Purchase order not found"));}
    private void requireStatus(PurchaseOrder po,PurchaseOrderStatus status,String message){if(po.getStatus()!=status)throw new IllegalArgumentException(message);}
    private String newNumber(){return "PO-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT);}
}
