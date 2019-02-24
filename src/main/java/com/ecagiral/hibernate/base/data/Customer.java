package com.ecagiral.hibernate.base.data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private Integer payment;

    @Version
    private Integer version;

    public Customer(){}

    public Customer(String name){
        this.name = name;
        this.payment = 0;
    }

    public Integer getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void makePayment(int amount) {
        this.payment = this.payment + amount;
    }

    public Integer getPayment(){
        return payment;
    }

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "buyer")
    public List<Sale> sales = new ArrayList<>();
}
