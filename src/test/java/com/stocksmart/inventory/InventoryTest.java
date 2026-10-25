package com.stocksmart.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.stocksmart.location.*;
import com.stocksmart.organization.Organization;
import com.stocksmart.product.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InventoryTest {
    @Test void tracksAvailableStockSeparatelyFromReserved(){
        var organization=new Organization("Corner Shop");
        var locationForm=new LocationForm();locationForm.setName("Main store");locationForm.setCode("MAIN");
        var productForm=new ProductForm();productForm.setName("Tea");productForm.setSku("TEA-1");productForm.setSellingPrice(BigDecimal.ONE);productForm.setUnit("box");productForm.setReorderLevel(BigDecimal.ZERO);
        var row=new Inventory(organization,new Product(organization,null,productForm),new Location(organization,locationForm));
        row.add(new BigDecimal("12.000"));row.reserve(new BigDecimal("4.000"));
        assertEquals(new BigDecimal("12.000"),row.getQuantity());
        assertEquals(new BigDecimal("4.000"),row.getReservedQuantity());
        assertEquals(new BigDecimal("8.000"),row.getAvailableQuantity());
        row.subtract(new BigDecimal("4.000"));row.release(new BigDecimal("4.000"));
        assertEquals(BigDecimal.ZERO.setScale(3),row.getAvailableQuantity());
    }
}
