package com.stocksmart.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductForm {
    @NotBlank @Size(max=160) private String name;
    @NotBlank @Size(max=80) private String sku;
    @Size(max=4000) private String description;
    @NotNull @DecimalMin("0.00") @Digits(integer=10,fraction=2) private BigDecimal sellingPrice=BigDecimal.ZERO;
    @DecimalMin("0.00") @Digits(integer=10,fraction=2) private BigDecimal costPrice;
    @Size(max=120) private String barcode;
    @NotBlank @Size(max=30) private String unit="each";
    @NotNull @DecimalMin("0.000") @Digits(integer=9,fraction=3) private BigDecimal reorderLevel=BigDecimal.ZERO;
    private Long categoryId;
    private boolean active=true;
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getSku(){return sku;} public void setSku(String v){sku=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public BigDecimal getSellingPrice(){return sellingPrice;} public void setSellingPrice(BigDecimal v){sellingPrice=v;}
    public BigDecimal getCostPrice(){return costPrice;} public void setCostPrice(BigDecimal v){costPrice=v;}
    public String getBarcode(){return barcode;} public void setBarcode(String v){barcode=v;}
    public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
    public BigDecimal getReorderLevel(){return reorderLevel;} public void setReorderLevel(BigDecimal v){reorderLevel=v;}
    public Long getCategoryId(){return categoryId;} public void setCategoryId(Long v){categoryId=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
