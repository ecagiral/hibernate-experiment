package com.ecagiral.hibernate.base.data;

import javax.persistence.*;

@Entity
public class Part {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Product parent;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Product child;

    private Integer timesUsed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getParent() {
        return parent;
    }

    public void setParent(Product parent) {
        this.parent = parent;
    }

    public Product getChild() {
        return child;
    }

    public void setChild(Product child) {
        this.child = child;
    }

    public Integer getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(Integer timesUsed) {
        this.timesUsed = timesUsed;
    }
}
