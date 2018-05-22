package com.ecagiral.hibernate.base.service;


import com.ecagiral.hibernate.base.Repo;
import com.ecagiral.hibernate.base.data.Customer;
import org.hibernate.Session;

import javax.persistence.LockModeType;
import java.util.logging.Logger;

public class PaymentJob implements Runnable{

    private Logger LOG = Logger.getLogger(PaymentJob.class.getName());

    private Integer id;
    private Integer customerId;
    private LockModeType lockType;

    public PaymentJob(Integer id, Integer customerId,LockModeType lockType){
        this.lockType = lockType;
        this.customerId = customerId;
        this.id = id;
    }

    public void run() {
        try {
            makePayment();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    public void makePayment() throws Exception {

        Session session = Repo.getInstance().getSession();
        session.beginTransaction();

        Customer customer = session.find(Customer.class,customerId, lockType);

        Thread.sleep(50);
        customer.makePayment(1);
        //LOG.info("Customer " + customer.getId()+" payment: " + customer.getPayment());
        session.update(customer);

        session.getTransaction().commit();
        session.close();

    }
}
