package com.ecagiral.hibernate.base.service;

import com.ecagiral.hibernate.base.data.Customer;

import javax.persistence.LockModeType;


public class ReportJob extends BaseJob{

    private Integer customerId;
    private LockModeType lockType;

    public ReportJob(Integer id,Long pre,Long post,Integer customerId,LockModeType lockType){
        super(id,pre,post);
        this.customerId = customerId;
        this.lockType = lockType;
    }

    public Integer execute() throws Exception{
        Customer customer = session.find(Customer.class,customerId, lockType);
        LOG.info("Customer " + customer.getId()+" payment: " + customer.getPayment());
        return customer.getPayment();
    }

}
