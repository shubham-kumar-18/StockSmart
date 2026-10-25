package com.stocksmart.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.stocksmart.location.*;
import com.stocksmart.organization.Organization;
import com.stocksmart.product.*;
import com.stocksmart.user.AppUser;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderItemTest {
    @Test void snapshotsIdentityAndRoundsLineTotal(){
        var organization=new Organization("Corner Shop");
        var locationForm=new LocationForm();locationForm.setName("Main store");locationForm.setCode("MAIN");
        var location=new Location(organization,locationForm);
        var productForm=new ProductForm();productForm.setName("Tea");productForm.setSku("TEA-1");productForm.setSellingPrice(new BigDecimal("2.35"));productForm.setCostPrice(new BigDecimal("1.00"));productForm.setUnit("box");productForm.setReorderLevel(BigDecimal.ZERO);
        var product=new Product(organization,null,productForm);
        var user=new AppUser(organization,"Admin","admin@example.test","bcrypt-hash");
        var order=new CustomerOrder(organization,location,"SS-12345678",user);
        order.addItem(new OrderItem(order,product,new BigDecimal("3"),product.getSellingPrice()));

        assertEquals("7.05",order.getSubtotal().toPlainString());
        assertEquals("Tea",order.getItems().getFirst().getProductName());
        assertEquals("TEA-1",order.getItems().getFirst().getSku());
        assertEquals(OrderStatus.DRAFT,order.getStatus());
    }
}
