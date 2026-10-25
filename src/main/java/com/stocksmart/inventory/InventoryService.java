package com.stocksmart.inventory;

import com.stocksmart.location.*;
import com.stocksmart.organization.OrganizationRepository;
import com.stocksmart.product.*;
import com.stocksmart.user.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    private final InventoryRepository inventory; private final StockMovementRepository movements;
    private final ProductRepository products; private final LocationRepository locations;
    private final OrganizationRepository organizations; private final AppUserRepository users;
    public InventoryService(InventoryRepository inventory,StockMovementRepository movements,ProductRepository products,LocationRepository locations,OrganizationRepository organizations,AppUserRepository users){this.inventory=inventory;this.movements=movements;this.products=products;this.locations=locations;this.organizations=organizations;this.users=users;}

    @Transactional(readOnly=true) public List<Inventory> all(Long org){return inventory.findWorkspaceInventory(org);}
    @Transactional(readOnly=true) public List<StockMovement> recent(Long org){return movements.recent(org,PageRequest.of(0,50));}
    @Transactional(readOnly=true) public BigDecimal totalUnits(Long org){return inventory.totalUnits(org);}
    @Transactional(readOnly=true) public long lowStockCount(Long org){return inventory.findWorkspaceInventory(org).stream().filter(i->i.getProduct().isActive()&&i.getAvailableQuantity().compareTo(i.getProduct().getReorderLevel())<=0).count();}
    @Transactional(readOnly=true) public long outOfStockCount(Long org){return inventory.findWorkspaceInventory(org).stream().filter(i->i.getProduct().isActive()&&i.getAvailableQuantity().signum()==0).count();}
    @Transactional(readOnly=true) public List<Product> activeProducts(Long org){return products.findActiveWorkspaceProducts(org);}
    @Transactional(readOnly=true) public List<Location> activeLocations(Long org){return locations.findAllByOrganization_IdOrderByName(org).stream().filter(Location::isActive).toList();}

    @Transactional public void opening(Long org,Long actorId,StockOperationForm f){
        var product=product(org,f.getProductId());var location=location(org,f.getLocationId());
        if(movements.existsByOrganization_IdAndProduct_IdAndMovementTypeAndDestinationLocation_Id(org,product.getId(),MovementType.OPENING,location.getId()))throw new IllegalArgumentException("Opening stock was already recorded for this product at this location.");
        var row=lockOrCreate(org,product,location);if(row.getQuantity().signum()!=0)throw new IllegalArgumentException("Opening stock requires a zero on-hand balance. Use a receipt or adjustment instead.");
        row.add(f.getQuantity());saveMovement(org,actorId,product,MovementType.OPENING,f.getQuantity(),null,location,f);
    }
    @Transactional public void receive(Long org,Long actorId,StockOperationForm f){requireReference(f,"A receipt reference is required to prevent duplicate processing.");var p=product(org,f.getProductId());var l=location(org,f.getLocationId());receive(org,actorId,p,l,f);}
    @Transactional public void receiveForPurchaseOrder(Long org,Long actorId,StockOperationForm f){requireReference(f,"A receipt reference is required to prevent duplicate processing.");var p=products.findByIdAndOrganization_Id(f.getProductId(),org).orElseThrow(()->new NoSuchElementException("Product not found"));var l=locations.findByIdAndOrganization_Id(f.getLocationId(),org).orElseThrow(()->new NoSuchElementException("Location not found"));receive(org,actorId,p,l,f);}
    @Transactional public void adjust(Long org,Long actorId,StockOperationForm f,boolean add){var p=product(org,f.getProductId());var l=location(org,f.getLocationId());var row=lockOrCreate(org,p,l);if(add){row.add(f.getQuantity());saveMovement(org,actorId,p,MovementType.ADJUSTMENT_IN,f.getQuantity(),null,l,f);}else{requireAvailable(row,f.getQuantity());row.subtract(f.getQuantity());saveMovement(org,actorId,p,MovementType.ADJUSTMENT_OUT,f.getQuantity(),l,null,f);}}
    @Transactional public void transfer(Long org,Long actorId,StockOperationForm f){
        requireReference(f,"A transfer reference is required to prevent duplicate processing.");
        if(f.getDestinationLocationId()==null||f.getLocationId().equals(f.getDestinationLocationId()))throw new IllegalArgumentException("Choose a different destination location.");
        var p=product(org,f.getProductId());var source=location(org,f.getLocationId());var destination=location(org,f.getDestinationLocationId());
        ensureReferenceUnique(org,p,MovementType.TRANSFER,f);
        Inventory from;Inventory to;
        if(source.getId()<destination.getId()){from=lockOrCreate(org,p,source);to=lockOrCreate(org,p,destination);}else{to=lockOrCreate(org,p,destination);from=lockOrCreate(org,p,source);}
        requireAvailable(from,f.getQuantity());from.subtract(f.getQuantity());to.add(f.getQuantity());
        saveMovement(org,actorId,p,MovementType.TRANSFER,f.getQuantity(),source,destination,f);
    }
    private Product product(Long org,Long id){var p=products.findByIdAndOrganization_Id(id,org).orElseThrow(()->new NoSuchElementException("Product not found"));if(!p.isActive())throw new IllegalArgumentException("Inactive products cannot be used for stock operations.");return p;}
    private Location location(Long org,Long id){var l=locations.findByIdAndOrganization_Id(id,org).orElseThrow(()->new NoSuchElementException("Location not found"));if(!l.isActive())throw new IllegalArgumentException("Inactive locations cannot be used for stock operations.");return l;}
    private Inventory lockOrCreate(Long org,Product p,Location l){
        if(inventory.findByOrganization_IdAndProduct_IdAndLocation_Id(org,p.getId(),l.getId()).isEmpty())inventory.saveAndFlush(new Inventory(organizations.findById(org).orElseThrow(),p,l));
        return inventory.lockRow(org,p.getId(),l.getId()).orElseThrow();
    }
    private void requireAvailable(Inventory row,BigDecimal amount){if(row.getAvailableQuantity().compareTo(amount)<0)throw new IllegalArgumentException("Not enough unreserved stock at the source location.");}
    private void receive(Long org,Long actorId,Product p,Location l,StockOperationForm f){ensureReferenceUnique(org,p,MovementType.RECEIPT,f);var row=lockOrCreate(org,p,l);row.add(f.getQuantity());saveMovement(org,actorId,p,MovementType.RECEIPT,f.getQuantity(),null,l,f);}
    private void requireReference(StockOperationForm f,String message){if(f.getReference()==null||f.getReference().isBlank())throw new IllegalArgumentException(message);}
    private void ensureReferenceUnique(Long org,Product product,MovementType type,StockOperationForm f){if(movements.existsByOrganization_IdAndMovementTypeAndProduct_IdAndReferenceIgnoreCase(org,type,product.getId(),f.getReference().trim()))throw new IllegalArgumentException("This reference has already been processed for this product.");}
    private void saveMovement(Long org,Long actorId,Product p,MovementType type,BigDecimal quantity,Location source,Location destination,StockOperationForm f){
        var actor=users.findById(actorId).orElseThrow();var organization=organizations.findById(org).orElseThrow();
        String reference=clean(f.getReference()),reason=clean(f.getReason());
        movements.save(new StockMovement(organization,p,type,quantity,source,destination,reference,reason,actor));
    }
    private String clean(String text){return text==null||text.isBlank()?null:text.trim();}
}
