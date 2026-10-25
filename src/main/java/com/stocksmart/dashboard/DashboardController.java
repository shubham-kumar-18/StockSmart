package com.stocksmart.dashboard;

import com.stocksmart.user.AppUserRepository;
import com.stocksmart.product.ProductRepository;
import com.stocksmart.location.LocationRepository;
import com.stocksmart.inventory.InventoryService;
import com.stocksmart.order.CustomerOrderRepository;
import com.stocksmart.order.OrderStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final AppUserRepository users;
    private final ProductRepository products;
    private final LocationRepository locations;
    private final InventoryService inventory;
    private final CustomerOrderRepository orders;
    public DashboardController(AppUserRepository users, ProductRepository products, LocationRepository locations, InventoryService inventory, CustomerOrderRepository orders) { this.users = users; this.products=products; this.locations=locations; this.inventory=inventory; this.orders=orders; }
    @GetMapping("/dashboard") public String dashboard(Authentication authentication, Model model) {
        var user = users.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("organization", user.getOrganization().getName());
        Long organizationId=user.getOrganization().getId();
        model.addAttribute("activeProducts", products.countByOrganization_IdAndActiveTrue(organizationId));
        model.addAttribute("locationsCount", locations.countByOrganization_Id(organizationId));
        model.addAttribute("totalUnits", inventory.totalUnits(organizationId));
        model.addAttribute("lowStockCount", inventory.lowStockCount(organizationId));
        model.addAttribute("outOfStockCount", inventory.outOfStockCount(organizationId));
        model.addAttribute("pendingOrders", orders.countByOrganization_IdAndStatusIn(organizationId, java.util.List.of(OrderStatus.DRAFT, OrderStatus.CONFIRMED)));
        return "dashboard";
    }
}
