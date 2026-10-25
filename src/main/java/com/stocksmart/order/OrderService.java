package com.stocksmart.order;

import com.stocksmart.inventory.*;
import com.stocksmart.location.*;
import com.stocksmart.organization.OrganizationRepository;
import com.stocksmart.product.*;
import com.stocksmart.user.AppUserRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final CustomerOrderRepository orders; private final InventoryRepository inventory;
    private final StockMovementRepository movements; private final ProductRepository products;
    private final LocationRepository locations; private final OrganizationRepository organizations;
    private final AppUserRepository users;
    public OrderService(CustomerOrderRepository orders,InventoryRepository inventory,StockMovementRepository movements,ProductRepository products,LocationRepository locations,OrganizationRepository organizations,AppUserRepository users){this.orders=orders;this.inventory=inventory;this.movements=movements;this.products=products;this.locations=locations;this.organizations=organizations;this.users=users;}

    @Transactional(readOnly=true) public List<CustomerOrder> all(Long org){return orders.findAllWorkspaceOrders(org);}
    @Transactional(readOnly=true) public CustomerOrder get(Long org,Long id){return orders.findByIdAndOrganization_Id(id,org).orElseThrow(()->new NoSuchElementException("Order not found"));}
    @Transactional public CustomerOrder create(Long org,Long userId,OrderForm form){
        var location=locations.findByIdAndOrganization_Id(form.getLocationId(),org).orElseThrow(()->new NoSuchElementException("Location not found"));
        if(!location.isActive())throw new IllegalArgumentException("Choose an active location.");
        if(form.getItems()==null||form.getItems().isEmpty())throw new IllegalArgumentException("Add at least one product to the order.");
        var order=new CustomerOrder(organizations.findById(org).orElseThrow(),location,newOrderNumber(),users.findById(userId).orElseThrow());
        for(var line:form.getItems()){
            if(line.getProductId()==null||line.getQuantity()==null||line.getQuantity().signum()<=0)continue;
            var product=products.findByIdAndOrganization_Id(line.getProductId(),org).orElseThrow(()->new NoSuchElementException("Product not found"));
            if(!product.isActive())throw new IllegalArgumentException("Inactive products cannot be ordered.");
            order.addItem(new OrderItem(order,product,line.getQuantity(),product.getSellingPrice()));
        }
        if(order.getItems().isEmpty())throw new IllegalArgumentException("Add at least one valid product and quantity.");
        return orders.save(order);
    }
    @Transactional public void confirm(Long org,Long id){
        var order=locked(org,id);requireStatus(order,OrderStatus.DRAFT,"Only draft orders can be confirmed.");
        if(order.getItems().stream().anyMatch(item->!item.getProduct().isActive()))throw new IllegalArgumentException("Remove inactive products before confirming this order.");
        for(var entry:quantities(order).entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()){
            var row=inventory.lockRow(org,entry.getKey(),order.getLocation().getId()).orElseThrow(()->new IllegalArgumentException("There is no stock for every item at the order location."));
            if(row.getAvailableQuantity().compareTo(entry.getValue())<0)throw new IllegalArgumentException("Available stock is insufficient to confirm this order.");
            row.reserve(entry.getValue());
        }
        order.setStatus(OrderStatus.CONFIRMED);
    }
    @Transactional public void fulfill(Long org,Long userId,Long id){
        var order=locked(org,id);requireStatus(order,OrderStatus.CONFIRMED,"Only confirmed orders can be fulfilled.");
        var actor=users.findById(userId).orElseThrow();var organization=organizations.findById(org).orElseThrow();
        for(var entry:quantities(order).entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()){
            var row=inventory.lockRow(org,entry.getKey(),order.getLocation().getId()).orElseThrow();
            BigDecimal amount=entry.getValue();if(row.getReservedQuantity().compareTo(amount)<0||row.getQuantity().compareTo(amount)<0)throw new IllegalStateException("Reserved stock does not match the order.");
            row.subtract(amount);row.release(amount);
            var product=products.findByIdAndOrganization_Id(entry.getKey(),org).orElseThrow();
            movements.save(new StockMovement(organization,product,MovementType.ORDER_FULFILLMENT,amount,order.getLocation(),null,order.getOrderNumber(),"Order fulfilled",actor));
        }
        order.setStatus(OrderStatus.FULFILLED);
    }
    @Transactional public void cancel(Long org,Long id){
        var order=locked(org,id);
        if(order.getStatus()==OrderStatus.FULFILLED||order.getStatus()==OrderStatus.CANCELLED)throw new IllegalArgumentException("This order can no longer be cancelled.");
        if(order.getStatus()==OrderStatus.CONFIRMED)for(var entry:quantities(order).entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()){
            var row=inventory.lockRow(org,entry.getKey(),order.getLocation().getId()).orElseThrow();
            if(row.getReservedQuantity().compareTo(entry.getValue())<0)throw new IllegalStateException("Reserved stock does not match the order.");row.release(entry.getValue());
        }
        order.setStatus(OrderStatus.CANCELLED);
    }
    private CustomerOrder locked(Long org,Long id){return orders.lockOrder(id,org).orElseThrow(()->new NoSuchElementException("Order not found"));}
    private void requireStatus(CustomerOrder o,OrderStatus status,String message){if(o.getStatus()!=status)throw new IllegalArgumentException(message);}
    private Map<Long,BigDecimal> quantities(CustomerOrder order){return order.getItems().stream().collect(Collectors.groupingBy(i->i.getProduct().getId(),Collectors.reducing(BigDecimal.ZERO,OrderItem::getQuantity,BigDecimal::add)));}
    private String newOrderNumber(){return "SS-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT);}
}
