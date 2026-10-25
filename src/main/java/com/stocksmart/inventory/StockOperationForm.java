package com.stocksmart.inventory;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class StockOperationForm {
    @NotNull private Long productId;
    @NotNull private Long locationId;
    private Long destinationLocationId;
    @NotNull @DecimalMin(value="0.001") @Digits(integer=9,fraction=3) private BigDecimal quantity;
    @Size(max=120) private String reference;
    @Size(max=500) private String reason;
    public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
    public Long getLocationId(){return locationId;} public void setLocationId(Long v){locationId=v;}
    public Long getDestinationLocationId(){return destinationLocationId;} public void setDestinationLocationId(Long v){destinationLocationId=v;}
    public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
    public String getReference(){return reference;} public void setReference(String v){reference=v;}
    public String getReason(){return reason;} public void setReason(String v){reason=v;}
}
