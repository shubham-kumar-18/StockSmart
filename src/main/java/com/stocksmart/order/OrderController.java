package com.stocksmart.order;

import com.stocksmart.inventory.InventoryService;
import com.stocksmart.security.CurrentWorkspace;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
    private final OrderService orders;private final InventoryService inventory;private final CurrentWorkspace workspace;
    public OrderController(OrderService orders,InventoryService inventory,CurrentWorkspace workspace){this.orders=orders;this.inventory=inventory;this.workspace=workspace;}
    @GetMapping("/orders") public String list(Authentication auth,Model model){model.addAttribute("orders",orders.all(workspace.organizationId(auth)));return "orders";}
    @GetMapping("/orders/new") public String createForm(Authentication auth,Model model){model.addAttribute("form",new OrderForm());fillOptions(auth,model);return "order-form";}
    @PostMapping("/orders") public String create(@Valid @ModelAttribute("form") OrderForm form,BindingResult result,Authentication auth,Model model,RedirectAttributes redirect){if(result.hasErrors()){fillOptions(auth,model);return "order-form";}try{orders.create(workspace.organizationId(auth),workspace.user(auth).getId(),form);redirect.addFlashAttribute("orderSuccess","Draft order created. Confirm it to reserve stock.");return "redirect:/orders";}catch(IllegalArgumentException ex){model.addAttribute("formError",ex.getMessage());fillOptions(auth,model);return "order-form";}}
    @PostMapping("/orders/{id}/confirm") public String confirm(@PathVariable Long id,Authentication auth,RedirectAttributes redirect){return act(auth,redirect,()->orders.confirm(workspace.organizationId(auth),id));}
    @PostMapping("/orders/{id}/fulfill") public String fulfill(@PathVariable Long id,Authentication auth,RedirectAttributes redirect){return act(auth,redirect,()->orders.fulfill(workspace.organizationId(auth),workspace.user(auth).getId(),id));}
    @PostMapping("/orders/{id}/cancel") public String cancel(@PathVariable Long id,Authentication auth,RedirectAttributes redirect){return act(auth,redirect,()->orders.cancel(workspace.organizationId(auth),id));}
    private String act(Authentication auth,RedirectAttributes redirect,Runnable action){try{action.run();redirect.addFlashAttribute("orderSuccess","Order status updated.");}catch(IllegalArgumentException ex){redirect.addFlashAttribute("orderError",ex.getMessage());}return "redirect:/orders";}
    private void fillOptions(Authentication auth,Model model){Long org=workspace.organizationId(auth);model.addAttribute("products",inventory.activeProducts(org));model.addAttribute("locations",inventory.activeLocations(org));}
}
