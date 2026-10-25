package com.stocksmart.product;

import com.stocksmart.category.*;
import com.stocksmart.organization.OrganizationRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository products; private final CategoryRepository categories; private final OrganizationRepository organizations;
    public ProductService(ProductRepository p,CategoryRepository c,OrganizationRepository o){products=p;categories=c;organizations=o;}
    @Transactional(readOnly=true) public List<Product> search(Long org,String query,String status){
        boolean filtered=query!=null&&!query.isBlank();String term=filtered?query.trim():null;
        if("active".equals(status))return filtered?products.searchActiveWorkspaceProducts(org,term):products.findActiveWorkspaceProducts(org);
        if("inactive".equals(status))return filtered?products.searchInactiveWorkspaceProducts(org,term):products.findInactiveWorkspaceProducts(org);
        return filtered?products.searchWorkspaceProducts(org,term):products.findWorkspaceProducts(org);
    }
    @Transactional(readOnly=true) public Product get(Long org,Long id){return products.findByIdAndOrganization_Id(id,org).orElseThrow(()->new NoSuchElementException("Product not found"));}
    @Transactional public Product save(Long org,Long id,ProductForm form){
        String sku=form.getSku().trim();
        boolean duplicateSku=id==null?products.existsBySkuIgnoreCaseAndOrganization_Id(sku,org):products.existsBySkuIgnoreCaseAndOrganization_IdAndIdNot(sku,org,id);
        if(duplicateSku) throw new IllegalArgumentException("That SKU is already in use in this workspace.");
        String barcode=form.getBarcode()==null?"":form.getBarcode().trim();
        boolean duplicateBarcode=!barcode.isBlank()&&(id==null?products.existsByBarcodeIgnoreCaseAndOrganization_Id(barcode,org):products.existsByBarcodeIgnoreCaseAndOrganization_IdAndIdNot(barcode,org,id));
        if(duplicateBarcode) throw new IllegalArgumentException("That barcode is already in use in this workspace.");
        var category=form.getCategoryId()==null?null:categories.findByIdAndOrganization_Id(form.getCategoryId(),org).orElseThrow(()->new IllegalArgumentException("Choose a category from this workspace."));
        Product p=id==null?new Product(organizations.findById(org).orElseThrow(),category,form):get(org,id);
        if(id!=null)p.update(category,form);
        return products.save(p);
    }
    @Transactional public void addCategory(Long org,String name){if(name==null||name.isBlank()||name.trim().length()>80)throw new IllegalArgumentException("Enter a category name up to 80 characters.");String clean=name.trim();if(categories.existsByNameIgnoreCaseAndOrganization_Id(clean,org))throw new IllegalArgumentException("That category already exists.");categories.save(new Category(organizations.findById(org).orElseThrow(),clean));}
    @Transactional(readOnly=true) public List<Category> categories(Long org){return categories.findAllByOrganization_IdOrderByName(org);}
    @Transactional(readOnly=true) public long count(Long org){return products.countByOrganization_Id(org);}
}
