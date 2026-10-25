package com.stocksmart.location;

import com.stocksmart.organization.Organization;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="locations", uniqueConstraints=@UniqueConstraint(name="uq_locations_org_code", columnNames={"organization_id","code"}))
public class Location {
    public enum Type { WAREHOUSE, STORE }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id",nullable=false) private Organization organization;
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=40) private String code;
    @Column(length=500) private String address;
    @Enumerated(EnumType.STRING) @Column(name="location_type",nullable=false,length=20) private Type type;
    @Column(name="contact_info",length=160) private String contactInfo;
    @Column(nullable=false) private boolean active;
    @Column(name="created_at",insertable=false,updatable=false) private LocalDateTime createdAt;
    protected Location() { }
    public Location(Organization org, LocationForm f){this.organization=org;update(f);}
    public void update(LocationForm f){name=f.getName().trim();code=f.getCode().trim();address=empty(f.getAddress());type=f.getType();contactInfo=empty(f.getContactInfo());active=f.isActive();}
    private String empty(String s){return s==null||s.isBlank()?null:s.trim();}
    public Long getId(){return id;} public String getName(){return name;} public String getCode(){return code;} public String getAddress(){return address;} public Type getType(){return type;} public String getContactInfo(){return contactInfo;} public boolean isActive(){return active;}
}
