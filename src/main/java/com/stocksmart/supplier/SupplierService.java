package com.stocksmart.supplier;

import com.stocksmart.organization.OrganizationRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService {
    private final SupplierRepository suppliers;private final OrganizationRepository organizations;
    public SupplierService(SupplierRepository s,OrganizationRepository o){suppliers=s;organizations=o;}
    @Transactional(readOnly=true) public List<Supplier> search(Long org,String q){return suppliers.search(org,q==null||q.isBlank()?null:q.trim());}
    @Transactional(readOnly=true) public List<Supplier> active(Long org){return suppliers.findAllByOrganization_IdAndActiveTrueOrderByName(org);}
    @Transactional(readOnly=true) public Supplier get(Long org,Long id){return suppliers.findByIdAndOrganization_Id(id,org).orElseThrow(()->new NoSuchElementException("Supplier not found"));}
    @Transactional public void save(Long org,Long id,SupplierForm form){var supplier=id==null?new Supplier(organizations.findById(org).orElseThrow(),form):get(org,id);if(id!=null)supplier.update(form);suppliers.save(supplier);}
}
