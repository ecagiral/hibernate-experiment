package com.ecagiral.hibernate.child;

import com.ecagiral.hibernate.base.Repo;
import com.ecagiral.hibernate.base.data.Customer;
import com.ecagiral.hibernate.base.data.Sale;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class OrphanTest {


    /**
     * Hibernate is unable to remove orphan
     * if added and removed to the parent's list in the same session
     */
    public void test_OrphanRemoval(){

        Session session = Repo.getInstance().getSession();
        session.beginTransaction();

        Customer customer = new Customer(UUID.randomUUID().toString());
        Sale sale = new Sale(customer,BigDecimal.TEN);
        customer.sales.add(sale);
        session.save(customer);

        customer.sales.remove(sale);

        List<Sale> sales = session
                .createQuery("select s from Sale s where s.buyer = :buyer",Sale.class)
                .setParameter("buyer",customer)
                .getResultList();
        int size = sales.size();

        session.getTransaction().commit();
        session.close();

        assertEquals("Sales count",1,size);
    }

    /**
     * Hibernate is able to remove orphan
     * if added to the parent's list
     * but removed via setting child's parent null
     */
    public void test_OrphanRemovalSetParentNull(){

        Session session = Repo.getInstance().getSession();
        session.beginTransaction();

        Customer customer = new Customer(UUID.randomUUID().toString());
        Sale sale = new Sale(customer,BigDecimal.TEN);
        customer.sales.add(sale);
        session.save(customer);

        sale.setBuyer(null);

        List<Sale> sales = session
                .createQuery("select s from Sale s where s.buyer = :buyer",Sale.class)
                .setParameter("buyer",customer)
                .getResultList();
        int size = sales.size();

        session.getTransaction().commit();
        session.close();

        assertEquals("Sales count",0,size);
    }

    /**
     * Hibernate is able to remove orphan
     * if added to the parent's list
     * flush called
     * and removed from the parent's list
     */
    public void test_OrphanRemovalWithFlush(){

        Session session = Repo.getInstance().getSession();
        session.beginTransaction();

        Customer customer = new Customer(UUID.randomUUID().toString());
        Sale sale = new Sale(customer,BigDecimal.TEN);
        customer.sales.add(sale);
        session.save(customer);

        session.flush();

        customer.sales.clear();
        session.save(customer);

        List<Sale> sales = session
                .createQuery("select s from Sale s where s.buyer = :buyer",Sale.class)
                .setParameter("buyer",customer)
                .getResultList();
        int size = sales.size();

        session.getTransaction().commit();
        session.close();

        assertEquals(0,size);
    }

    public void test_ChildCreation(){

        Session session = Repo.getInstance().getSession();
        session.beginTransaction();

        Customer customer = new Customer(UUID.randomUUID().toString());
        Sale sale = new Sale(customer,BigDecimal.TEN);
        customer.sales.add(sale);
        session.save(customer);

        Sale sale2 = new Sale(customer,BigDecimal.ONE);
        customer.sales.add(sale2);
        session.save(customer);

        List<Sale> sales = session
                .createQuery("select s from Sale s where s.buyer = :buyer",Sale.class)
                .setParameter("buyer",customer)
                .getResultList();
        int size = sales.size();

        session.getTransaction().commit();
        session.close();

        assertEquals(2,size);
    }
}
