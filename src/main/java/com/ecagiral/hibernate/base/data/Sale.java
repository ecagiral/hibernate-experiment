package com.ecagiral.hibernate.base.data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Sale {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Customer buyer;

    private BigDecimal price;

    public Sale(){}

    public Sale(Customer buyer, BigDecimal price){
        this.buyer = buyer;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public Customer getBuyer() {
        return buyer;
    }

    public void setBuyer(Customer buyer) {
        this.buyer = buyer;
    }

    public String toString(){
        return id+" "+price.toPlainString();
    }
}
