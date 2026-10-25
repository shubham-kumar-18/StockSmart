package com.stocksmart.purchasing;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PurchaseOrderLineForm {
    @NotNull private Long productId;
    @NotNull @DecimalMin("0.001") @Digits(integer=9,fraction=3) private BigDecimal quantity;
    @NotNull @DecimalMin("0.00") @Digits(integer=10,fraction=2) private BigDecimal unitCost;
    public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
    public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
    public BigDecimal getUnitCost(){return unitCost;} public void setUnitCost(BigDecimal v){unitCost=v;}
}
