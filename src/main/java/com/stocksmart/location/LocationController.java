package com.stocksmart.location;

import com.stocksmart.security.CurrentWorkspace;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class LocationController {
    private final LocationService service;private final CurrentWorkspace workspace;
    public LocationController(LocationService s,CurrentWorkspace w){service=s;workspace=w;}
    @GetMapping("/locations") public String list(Authentication a,Model m){m.addAttribute("locations",service.all(workspace.organizationId(a)));return "locations";}
    @GetMapping("/locations/new") public String form(Model m){m.addAttribute("form",new LocationForm());return "location-form";}
    @GetMapping("/locations/{id}/edit") public String edit(@PathVariable Long id,Authentication a,Model m){var l=service.all(workspace.organizationId(a)).stream().filter(x->x.getId().equals(id)).findFirst().orElseThrow();var f=new LocationForm();f.setName(l.getName());f.setCode(l.getCode());f.setAddress(l.getAddress());f.setType(l.getType());f.setContactInfo(l.getContactInfo());f.setActive(l.isActive());m.addAttribute("form",f);m.addAttribute("locationId",id);return "location-form";}
    @PostMapping("/locations") public String create(@Valid @ModelAttribute("form") LocationForm f,BindingResult r,Authentication a,Model m){return save(null,f,r,a,m);}
    @PostMapping("/locations/{id}") public String update(@PathVariable Long id,@Valid @ModelAttribute("form") LocationForm f,BindingResult r,Authentication a,Model m){return save(id,f,r,a,m);}
    private String save(Long id,LocationForm f,BindingResult r,Authentication a,Model m){if(r.hasErrors()){m.addAttribute("locationId",id);return "location-form";}try{service.save(workspace.organizationId(a),id,f);return "redirect:/locations?saved";}catch(IllegalArgumentException e){m.addAttribute("formError",e.getMessage());m.addAttribute("locationId",id);return "location-form";}}
}
