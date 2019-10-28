package com.ecagiral.hibernate.base.data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy="parent")
    private List<Part> bom = new ArrayList<>();

    public Product(){}

    public Product(String name){
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Part> getBom() {
        return bom;
    }

    public void setBom(List<Part> bom) {
        this.bom = bom;
    }

    public int getEdgeCount(){
        int total = 0;
        if(bom.size()==0){
            return 1;
        }
        for(Part part:bom){
            total = total + part.getChild().getEdgeCount()*part.getTimesUsed();
        }
        return total;
    }

    public void buildParts(List<ProductRef> productList){
        for(ProductRef aProduct:productList){
            if(aProduct.id.equals(id)){
                name = aProduct.name;
                Product component = new Product();
                component.id = aProduct.child_id;
                Part part = new Part();
                part.setParent(this);
                part.setTimesUsed(aProduct.timesused);
                part.setChild(component);
                bom.add(part);
                component.buildParts(productList);
            }
        }
    }

}
