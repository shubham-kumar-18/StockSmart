package com.stocksmart.order;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class OrderItemForm {
    @NotNull private Long productId;
    @NotNull @DecimalMin(value="0.001") @Digits(integer=9,fraction=3) private BigDecimal quantity;
    public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
    public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
}
