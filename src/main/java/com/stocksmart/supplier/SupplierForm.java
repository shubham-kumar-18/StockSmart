package com.stocksmart.supplier;

import jakarta.validation.constraints.*;

public class SupplierForm {
    @NotBlank @Size(max=160) private String name;
    @Size(max=120) private String contactPerson;
    @Email @Size(max=254) private String email;
    @Size(max=50) private String phone;
    @Size(max=500) private String address;
    @Size(max=4000) private String notes;
    private boolean active=true;
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getContactPerson(){return contactPerson;} public void setContactPerson(String v){contactPerson=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
