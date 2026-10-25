package com.stocksmart.location;

import com.stocksmart.organization.OrganizationRepository;
import com.stocksmart.inventory.InventoryRepository;
import com.stocksmart.order.*;
import com.stocksmart.purchasing.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {
    private final LocationRepository locations;private final OrganizationRepository organizations;private final InventoryRepository inventory;private final CustomerOrderRepository orders;private final PurchaseOrderRepository purchaseOrders;
    public LocationService(LocationRepository l,OrganizationRepository o,InventoryRepository inventory,CustomerOrderRepository orders,PurchaseOrderRepository purchaseOrders){locations=l;organizations=o;this.inventory=inventory;this.orders=orders;this.purchaseOrders=purchaseOrders;}
    @Transactional(readOnly=true) public List<Location> all(Long org){return locations.findAllByOrganization_IdOrderByName(org);}
    @Transactional public void save(Long org,Long id,LocationForm form){String code=form.getCode().trim();boolean duplicate=id==null?locations.existsByCodeIgnoreCaseAndOrganization_Id(code,org):locations.existsByCodeIgnoreCaseAndOrganization_IdAndIdNot(code,org,id);if(duplicate)throw new IllegalArgumentException("That location code is already in use.");Location l=id==null?new Location(organizations.findById(org).orElseThrow(),form):locations.findByIdAndOrganization_Id(id,org).orElseThrow(()->new NoSuchElementException("Location not found"));if(id!=null&&l.isActive()&&!form.isActive()){if(inventory.existsByOrganization_IdAndLocation_IdAndQuantityGreaterThan(org,id,java.math.BigDecimal.ZERO))throw new IllegalArgumentException("A location with on-hand stock cannot be deactivated.");if(orders.existsByOrganization_IdAndLocation_IdAndStatusIn(org,id,java.util.List.of(OrderStatus.DRAFT,OrderStatus.CONFIRMED)))throw new IllegalArgumentException("A location with open customer orders cannot be deactivated.");if(purchaseOrders.existsByOrganization_IdAndLocation_IdAndStatusIn(org,id,java.util.List.of(PurchaseOrderStatus.DRAFT,PurchaseOrderStatus.ORDERED,PurchaseOrderStatus.PARTIALLY_RECEIVED)))throw new IllegalArgumentException("A location with open purchase orders cannot be deactivated.");}if(id!=null)l.update(form);locations.save(l);}
}
