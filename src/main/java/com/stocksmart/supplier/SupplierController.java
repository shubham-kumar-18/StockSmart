package com.stocksmart.supplier;

import com.stocksmart.security.CurrentWorkspace;
import com.stocksmart.purchasing.PurchaseOrderRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SupplierController {
    private final SupplierService service;private final CurrentWorkspace workspace;private final PurchaseOrderRepository purchaseOrders;
    public SupplierController(SupplierService service,CurrentWorkspace workspace,PurchaseOrderRepository purchaseOrders){this.service=service;this.workspace=workspace;this.purchaseOrders=purchaseOrders;}
    @GetMapping("/suppliers") public String list(@RequestParam(required=false) String q,Authentication auth,Model model){model.addAttribute("suppliers",service.search(workspace.organizationId(auth),q));model.addAttribute("q",q);return "suppliers";}
    @GetMapping("/suppliers/new") public String create(Model model){model.addAttribute("form",new SupplierForm());return "supplier-form";}
    @GetMapping("/suppliers/{id}/edit") public String edit(@PathVariable Long id,Authentication auth,Model model){var s=service.get(workspace.organizationId(auth),id);var f=new SupplierForm();f.setName(s.getName());f.setContactPerson(s.getContactPerson());f.setEmail(s.getEmail());f.setPhone(s.getPhone());f.setAddress(s.getAddress());f.setNotes(s.getNotes());f.setActive(s.isActive());model.addAttribute("form",f);model.addAttribute("supplierId",id);return "supplier-form";}
    @GetMapping("/suppliers/{id}") public String details(@PathVariable Long id,Authentication auth,Model model){Long org=workspace.organizationId(auth);model.addAttribute("supplier",service.get(org,id));model.addAttribute("purchaseOrders",purchaseOrders.findAllByOrganization_IdAndSupplier_IdOrderByCreatedAtDesc(org,id));return "supplier-details";}
    @PostMapping("/suppliers") public String createPost(@Valid @ModelAttribute("form") SupplierForm form,BindingResult result,Authentication auth,Model model){return save(null,form,result,auth,model);}
    @PostMapping("/suppliers/{id}") public String update(@PathVariable Long id,@Valid @ModelAttribute("form") SupplierForm form,BindingResult result,Authentication auth,Model model){return save(id,form,result,auth,model);}
    private String save(Long id,SupplierForm form,BindingResult result,Authentication auth,Model model){if(result.hasErrors()){model.addAttribute("supplierId",id);return "supplier-form";}service.save(workspace.organizationId(auth),id,form);return "redirect:/suppliers?saved";}
}
