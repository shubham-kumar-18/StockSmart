package com.stocksmart.purchasing;

import com.stocksmart.inventory.InventoryService;
import com.stocksmart.security.CurrentWorkspace;
import com.stocksmart.supplier.SupplierService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PurchaseOrderController {
    private final PurchasingService service;private final InventoryService inventory;private final SupplierService suppliers;private final CurrentWorkspace workspace;
    public PurchaseOrderController(PurchasingService service,InventoryService inventory,SupplierService suppliers,CurrentWorkspace workspace){this.service=service;this.inventory=inventory;this.suppliers=suppliers;this.workspace=workspace;}
    @GetMapping("/purchase-orders") public String list(Authentication auth,Model model){model.addAttribute("purchaseOrders",service.all(workspace.organizationId(auth)));return "purchase-orders";}
    @GetMapping("/purchase-orders/new") public String form(Authentication auth,Model model){model.addAttribute("form",new PurchaseOrderForm());fill(auth,model);return "purchase-order-form";}
    @PostMapping("/purchase-orders") public String create(@Valid @ModelAttribute("form") PurchaseOrderForm form,BindingResult result,Authentication auth,Model model,RedirectAttributes redirect){if(result.hasErrors()){fill(auth,model);return "purchase-order-form";}try{service.create(workspace.organizationId(auth),workspace.user(auth).getId(),form);redirect.addFlashAttribute("purchaseSuccess","Draft purchase order created.");return "redirect:/purchase-orders";}catch(IllegalArgumentException ex){model.addAttribute("formError",ex.getMessage());fill(auth,model);return "purchase-order-form";}}
    @PostMapping("/purchase-orders/{id}/place") public String place(@PathVariable Long id,Authentication auth,RedirectAttributes redirect){return act(auth,redirect,()->service.place(workspace.organizationId(auth),id));}
    @PostMapping("/purchase-orders/{id}/cancel") public String cancel(@PathVariable Long id,Authentication auth,RedirectAttributes redirect){return act(auth,redirect,()->service.cancel(workspace.organizationId(auth),id));}
    @PostMapping("/purchase-orders/{id}/receipts") public String receive(@PathVariable Long id,@Valid @ModelAttribute GoodsReceiptForm form,BindingResult result,Authentication auth,RedirectAttributes redirect){if(result.hasErrors()){redirect.addFlashAttribute("purchaseError","Enter a receipt reference and a positive quantity.");return "redirect:/purchase-orders";}return act(auth,redirect,()->service.receive(workspace.organizationId(auth),workspace.user(auth).getId(),id,form));}
    private String act(Authentication auth,RedirectAttributes redirect,Runnable action){try{action.run();redirect.addFlashAttribute("purchaseSuccess","Purchase order updated.");}catch(IllegalArgumentException ex){redirect.addFlashAttribute("purchaseError",ex.getMessage());}return "redirect:/purchase-orders";}
    private void fill(Authentication auth,Model model){Long org=workspace.organizationId(auth);model.addAttribute("suppliers",suppliers.active(org));model.addAttribute("products",inventory.activeProducts(org));model.addAttribute("locations",inventory.activeLocations(org));}
}
