package com.ecagiral.hibernate.base.service;


import com.ecagiral.hibernate.base.data.Customer;

import jakarta.persistence.LockModeType;

public class PaymentJob extends BaseJob{

    private Integer customerId;
    private LockModeType lockType;

    public PaymentJob(Long pre, Long post,Integer id, Integer customerId,LockModeType lockType){
        super(id,pre,post);
        this.lockType = lockType;
        this.customerId = customerId;
    }

    public Integer execute() throws Exception {
        Customer customer = session.find(Customer.class,customerId, lockType);
        customer.makePayment(1);
        return customer.getPayment();
    }
}
