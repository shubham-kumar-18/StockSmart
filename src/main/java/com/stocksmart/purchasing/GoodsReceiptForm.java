package com.stocksmart.purchasing;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class GoodsReceiptForm {
    @NotNull private Long itemId;
    @NotBlank @Size(max=120) private String reference;
    @NotNull @DecimalMin("0.001") @Digits(integer=9,fraction=3) private BigDecimal quantity;
    public Long getItemId(){return itemId;} public void setItemId(Long v){itemId=v;}
    public String getReference(){return reference;} public void setReference(String v){reference=v;}
    public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
}
