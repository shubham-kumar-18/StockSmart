package com.stocksmart.product;

import com.stocksmart.security.CurrentWorkspace;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {
    private final ProductService service;private final CurrentWorkspace workspace;
    public ProductController(ProductService s,CurrentWorkspace w){service=s;workspace=w;}
    @GetMapping("/products") public String list(@RequestParam(required=false) String q,@RequestParam(defaultValue="all") String status,Authentication a,Model m){Long org=workspace.organizationId(a);m.addAttribute("products",service.search(org,q,status));m.addAttribute("categories",service.categories(org));m.addAttribute("q",q);m.addAttribute("status",status);m.addAttribute("productCount",service.count(org));return "products";}
    @GetMapping("/products/new") public String create(Model m,Authentication a){m.addAttribute("form",new ProductForm());m.addAttribute("categories",service.categories(workspace.organizationId(a)));return "product-form";}
    @GetMapping("/products/{id}/edit") public String edit(@PathVariable Long id,Model m,Authentication a){var p=service.get(workspace.organizationId(a),id);var f=new ProductForm();f.setName(p.getName());f.setSku(p.getSku());f.setDescription(p.getDescription());f.setSellingPrice(p.getSellingPrice());f.setCostPrice(p.getCostPrice());f.setBarcode(p.getBarcode());f.setUnit(p.getUnit());f.setReorderLevel(p.getReorderLevel());f.setActive(p.isActive());if(p.getCategory()!=null)f.setCategoryId(p.getCategory().getId());m.addAttribute("form",f);m.addAttribute("productId",id);m.addAttribute("categories",service.categories(workspace.organizationId(a)));return "product-form";}
    @PostMapping("/products") public String createPost(@Valid @ModelAttribute("form") ProductForm f,BindingResult r,Authentication a,Model m){return save(null,f,r,a,m);}
    @PostMapping("/products/{id}") public String update(@PathVariable Long id,@Valid @ModelAttribute("form") ProductForm f,BindingResult r,Authentication a,Model m){return save(id,f,r,a,m);}
    private String save(Long id,ProductForm f,BindingResult r,Authentication a,Model m){Long org=workspace.organizationId(a);if(r.hasErrors()){m.addAttribute("categories",service.categories(org));m.addAttribute("productId",id);return "product-form";}try{service.save(org,id,f);return "redirect:/products?success";}catch(IllegalArgumentException e){m.addAttribute("formError",e.getMessage());m.addAttribute("categories",service.categories(org));m.addAttribute("productId",id);return "product-form";}}
    @PostMapping("/categories") public String addCategory(@RequestParam String name,Authentication a){try{service.addCategory(workspace.organizationId(a),name);return "redirect:/products?categoryAdded";}catch(IllegalArgumentException e){return "redirect:/products?categoryError";}}
}
