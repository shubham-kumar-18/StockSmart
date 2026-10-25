package com.stocksmart.supplier;

import com.stocksmart.organization.Organization;
import jakarta.persistence.*;

@Entity @Table(name="suppliers")
public class Supplier {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id",nullable=false) private Organization organization;
    @Column(nullable=false,length=160) private String name;
    @Column(name="contact_person",length=120) private String contactPerson;
    @Column(length=254) private String email;
    @Column(length=50) private String phone;
    @Column(length=500) private String address;
    @Column(columnDefinition="TEXT") private String notes;
    @Column(nullable=false) private boolean active;
    protected Supplier() { }
    public Supplier(Organization organization,SupplierForm form){this.organization=organization;update(form);}
    public void update(SupplierForm f){name=f.getName().trim();contactPerson=clean(f.getContactPerson());email=clean(f.getEmail());phone=clean(f.getPhone());address=clean(f.getAddress());notes=clean(f.getNotes());active=f.isActive();}
    private String clean(String v){return v==null||v.isBlank()?null:v.trim();}
    public Long getId(){return id;} public Long getOrganizationId(){return organization.getId();} public String getName(){return name;} public String getContactPerson(){return contactPerson;} public String getEmail(){return email;} public String getPhone(){return phone;} public String getAddress(){return address;} public String getNotes(){return notes;} public boolean isActive(){return active;}
}
