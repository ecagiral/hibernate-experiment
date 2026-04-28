package com.ecagiral.hibernate.base.data;

public class ProductRef {

    Integer id;

    String name;

    Integer timesused;

    Integer child_id;

    public ProductRef() {}

    public ProductRef(Integer id, String name, Integer timesused, Integer child_id) {
        this.id = id;
        this.name = name;
        this.timesused = timesused;
        this.child_id = child_id;
    }

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
