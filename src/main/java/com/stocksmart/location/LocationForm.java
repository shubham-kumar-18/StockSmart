package com.stocksmart.location;

import jakarta.validation.constraints.*;

public class LocationForm {
    @NotBlank @Size(max=120) private String name;
    @NotBlank @Size(max=40) private String code;
    @Size(max=500) private String address;
    @NotNull private Location.Type type=Location.Type.WAREHOUSE;
    @Size(max=160) private String contactInfo;
    private boolean active=true;
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public Location.Type getType(){return type;} public void setType(Location.Type v){type=v;}
    public String getContactInfo(){return contactInfo;} public void setContactInfo(String v){contactInfo=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
