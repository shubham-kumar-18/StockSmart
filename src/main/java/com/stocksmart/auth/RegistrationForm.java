package com.stocksmart.auth;

import jakarta.validation.constraints.*;

public class RegistrationForm {
    @NotBlank @Size(max = 120) private String fullName;
    @NotBlank @Email @Size(max = 254) private String email;
    @NotBlank @Size(min = 8, max = 72) private String password;
    @NotBlank private String confirmPassword;
    @NotBlank @Size(max = 120) private String organizationName;
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
}
