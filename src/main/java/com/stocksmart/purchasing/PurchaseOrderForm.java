package com.stocksmart.purchasing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;

public class PurchaseOrderForm {
    @NotNull private Long supplierId;
    @NotNull private Long locationId;
    @Valid @Size(min=1,max=30) private List<PurchaseOrderLineForm> items=new ArrayList<>(List.of(new PurchaseOrderLineForm()));
    public Long getSupplierId(){return supplierId;} public void setSupplierId(Long v){supplierId=v;}
    public Long getLocationId(){return locationId;} public void setLocationId(Long v){locationId=v;}
    public List<PurchaseOrderLineForm> getItems(){return items;} public void setItems(List<PurchaseOrderLineForm> v){items=v;}
}
