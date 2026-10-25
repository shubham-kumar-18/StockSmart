package com.stocksmart.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;

public class OrderForm {
    @NotNull private Long locationId;
    @Valid @Size(min=1,max=30) private List<OrderItemForm> items=new ArrayList<>(List.of(new OrderItemForm()));
    public Long getLocationId(){return locationId;} public void setLocationId(Long v){locationId=v;}
    public List<OrderItemForm> getItems(){return items;} public void setItems(List<OrderItemForm> v){items=v;}
}
