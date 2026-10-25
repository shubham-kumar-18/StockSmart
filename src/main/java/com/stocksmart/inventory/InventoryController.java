package com.stocksmart.inventory;

import com.stocksmart.security.CurrentWorkspace;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InventoryController {
    private final InventoryService service;private final CurrentWorkspace workspace;
    public InventoryController(InventoryService service,CurrentWorkspace workspace){this.service=service;this.workspace=workspace;}

    @GetMapping("/inventory") public String index(Authentication auth,Model model){Long org=workspace.organizationId(auth);model.addAttribute("inventory",service.all(org));model.addAttribute("movements",service.recent(org));model.addAttribute("products",service.activeProducts(org));model.addAttribute("locations",service.activeLocations(org));model.addAttribute("totalUnits",service.totalUnits(org));model.addAttribute("lowStockCount",service.lowStockCount(org));model.addAttribute("form",new StockOperationForm());return "inventory";}
    @PostMapping("/inventory/opening") public String opening(@Valid @ModelAttribute("form") StockOperationForm form,BindingResult result,Authentication auth,RedirectAttributes redirect){if(result.hasErrors())return invalid(result,redirect);return execute(auth,redirect,()->service.opening(workspace.organizationId(auth),workspace.user(auth).getId(),form));}
    @PostMapping("/inventory/receipts") public String receipt(@Valid @ModelAttribute("form") StockOperationForm form,BindingResult result,Authentication auth,RedirectAttributes redirect){if(result.hasErrors())return invalid(result,redirect);return execute(auth,redirect,()->service.receive(workspace.organizationId(auth),workspace.user(auth).getId(),form));}
    @PostMapping("/inventory/adjustments/in") public String adjustmentIn(@Valid @ModelAttribute("form") StockOperationForm form,BindingResult result,Authentication auth,RedirectAttributes redirect){if(result.hasErrors())return invalid(result,redirect);return execute(auth,redirect,()->service.adjust(workspace.organizationId(auth),workspace.user(auth).getId(),form,true));}
    @PostMapping("/inventory/adjustments/out") public String adjustmentOut(@Valid @ModelAttribute("form") StockOperationForm form,BindingResult result,Authentication auth,RedirectAttributes redirect){if(result.hasErrors())return invalid(result,redirect);return execute(auth,redirect,()->service.adjust(workspace.organizationId(auth),workspace.user(auth).getId(),form,false));}
    @PostMapping("/inventory/transfers") public String transfer(@Valid @ModelAttribute("form") StockOperationForm form,BindingResult result,Authentication auth,RedirectAttributes redirect){if(result.hasErrors())return invalid(result,redirect);return execute(auth,redirect,()->service.transfer(workspace.organizationId(auth),workspace.user(auth).getId(),form));}
    private String invalid(BindingResult result,RedirectAttributes redirect){redirect.addFlashAttribute("operationError","Check the selected records and enter a positive quantity with at most three decimal places.");return "redirect:/inventory";}
    private String execute(Authentication auth,RedirectAttributes redirect,Runnable operation){try{operation.run();redirect.addFlashAttribute("operationSuccess","Stock operation recorded.");}catch(IllegalArgumentException ex){redirect.addFlashAttribute("operationError",ex.getMessage());}return "redirect:/inventory";}
}
