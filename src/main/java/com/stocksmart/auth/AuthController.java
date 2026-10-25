package com.stocksmart.auth;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final RegistrationService registrationService;
    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/") public String home() { return "redirect:/dashboard"; }
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/register") public String registration(Model model) {
        model.addAttribute("form", new RegistrationForm()); return "register";
    }
    @PostMapping("/register") public String register(@Valid @ModelAttribute("form") RegistrationForm form,
                                                       BindingResult result, Model model) {
        if (result.hasErrors()) return "register";
        try { registrationService.register(form); }
        catch (IllegalArgumentException ex) { model.addAttribute("registrationError", ex.getMessage()); return "register"; }
        return "redirect:/login?registered";
    }
}
