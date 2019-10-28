package com.ecagiral.hibernate.base.data;

public class ProductRef {

    Integer id;

    String name;

    Integer timesused;

    Integer child_id;

    @Override
    public String toString() {
        return "ProductRef{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timesused=" + timesused +
                ", child_id=" + child_id +
                '}';
    }
}
