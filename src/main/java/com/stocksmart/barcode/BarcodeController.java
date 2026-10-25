package com.stocksmart.barcode;

import com.stocksmart.inventory.InventoryRepository;
import com.stocksmart.product.ProductRepository;
import com.stocksmart.security.CurrentWorkspace;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BarcodeController {
    private final ProductRepository products;private final InventoryRepository inventory;private final CurrentWorkspace workspace;
    public BarcodeController(ProductRepository products,InventoryRepository inventory,CurrentWorkspace workspace){this.products=products;this.inventory=inventory;this.workspace=workspace;}
    @GetMapping("/barcode") public String page(@RequestParam(required=false) String code,Authentication auth,Model model){model.addAttribute("code",code);if(code!=null&&!code.isBlank()){Long org=workspace.organizationId(auth);var product=products.findByBarcodeIgnoreCaseAndOrganization_Id(code.trim(),org);if(product.isPresent()){model.addAttribute("product",product.get());model.addAttribute("stock",inventory.findAllByOrganization_IdAndProduct_IdOrderByLocation_Name(org,product.get().getId()));}else model.addAttribute("lookupError","No product matches that barcode in this workspace.");}return "barcode";}
}
